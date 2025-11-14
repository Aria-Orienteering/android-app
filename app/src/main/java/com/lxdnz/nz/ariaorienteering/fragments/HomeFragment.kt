package com.lxdnz.nz.ariaorienteering.fragments

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

import com.lxdnz.nz.ariaorienteering.R
import com.lxdnz.nz.ariaorienteering.databinding.FragmentHomeBinding
import com.lxdnz.nz.ariaorienteering.model.Course
import com.lxdnz.nz.ariaorienteering.model.Marker
import com.lxdnz.nz.ariaorienteering.model.User
import com.lxdnz.nz.ariaorienteering.model.types.ImageType
import com.lxdnz.nz.ariaorienteering.model.types.MarkerStatus
import com.lxdnz.nz.ariaorienteering.services.LocationService
import com.lxdnz.nz.ariaorienteering.viewmodel.UserViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private var gameTime:Long = 0

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val TAG = "Home Fragment"
    private var toastCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // make changes here to id'd view items
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userViewModel: UserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val userLiveData = userViewModel.getLiveUserData()
        userLiveData.observe(this, Observer { user: User? ->
            if (user != null) {
                Log.i(TAG, "update from Observer")
                updateUI(user)
            }
        })

        activateButton(binding.startActionButton)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateUI(user: User) {
        binding.homeText.text = getString(R.string.change_home) + ' ' + user.firstName
        if (user.courseObject != null) {
            // check for All course markers complete then stop timer
            if (checkMarkersFound(user.courseObject?.markers)) {

                // activate home marker
                if (user.homeMarker != null) {
                    if (!user.homeActive && toastCount == 0) {
                        Toast.makeText(requireContext(), "All markers found, Head for Home", Toast.LENGTH_SHORT).show()
                        User.addHomeMarker(user.homeMarker, true)
                        toastCount++
                    } else {
                        Log.i(TAG, "found markers, home Active")
                        if (user.homeMarker!!.status.equals(MarkerStatus.FOUND) && toastCount == 1) {
                            // do this bit when home marker is found
                            binding.timerMeter.stop()
                            Toast.makeText(requireContext(), "You have finished", Toast.LENGTH_SHORT).show()
                            toastCount++
                            // make startButton visible
                            binding.startActionButton.visibility = View.VISIBLE
                            binding.startText.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        when (toastCount)
        {
            0 -> {if (user.courseObject != null){
                binding.courseSelected.text = getString(R.string.select_course) + ' ' + user.courseObject!!.id
                } else {
                binding.courseSelected.text = getString(R.string.no_course)
            }
            }
            1 -> {binding.courseSelected.text = "Congratulations! " + user.firstName + " you found all the markers. Head for Home"}
            else -> {binding.courseSelected.text = "Congratulations! " + user.firstName + " you made it Home"
                // update user status
                if (user.homeActive) {
                    Log.i(TAG, "finishing course")
                    User.finishCourse(binding.timerMeter.text.toString())
                }
            }
        }

    }

    private fun checkMarkersFound(markers: MutableList<Marker>?): Boolean {

        return markers!!.all{marker -> marker.status.equals(MarkerStatus.FOUND)  }
    }

    private fun activateButton(button: FloatingActionButton) {
        var dX = 0f
        var dY = 0f
        var startX = 0f
        var startY = 0f
        button.setOnTouchListener( {v: View, event: MotionEvent ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    startX = event.rawX
                    startY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    v.setY(event.rawY + dY)
                    v.setX(event.rawX + dX)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (Math.abs(startX - event.rawX) < 10 && Math.abs(startY - event.rawY)  < 10) {
                        when(button) {
                            binding.startActionButton -> {
                                Toast.makeText(v.context, "Selecting Random Course", Toast.LENGTH_SHORT).show()
                                selectRandomCourse()
                                startTimer()
                            }
                        }

                    }
                    true
                }
                else -> false
            }

        })

    }


    private fun selectRandomCourse() {
        // requires a home target, sent to Firebase as one update to limit calls to DB
        val currentLocation = LocationService(requireContext()).getLocation()

        val homeMarker = Marker(1000, ImageType.DEFAULT, currentLocation!!.latitude, currentLocation.longitude)

        lifecycleScope.launch {
            try {
                val course = Course.selectRandomCourse().await()
                homeMarker.status = MarkerStatus.NOT_FOUND
                User.addCourse(course, homeMarker)
            } catch (e: Exception) {
                Log.e(TAG, "Error selecting random course", e)
                Toast.makeText(requireContext(), "Error selecting course", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startTimer() {

        //start timer
        binding.timerMeter.base = SystemClock.elapsedRealtime() + gameTime
        binding.timerMeter.start()
        toastCount == 0
        // make start button inaccessible
        binding.startActionButton.visibility = View.GONE
        binding.startText.visibility = View.GONE
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onResume() {
        super.onResume()
        getCurrentUser()
    }

    private fun getCurrentUser() {
        User.retrieve(FirebaseAuth.getInstance().currentUser?.uid)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
