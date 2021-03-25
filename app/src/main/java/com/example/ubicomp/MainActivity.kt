package com.example.ubicomp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ubicomp.Utils.Constants
import com.example.ubicomp.Utils.Constants.SUBMITTED
import com.example.ubicomp.Utils.Constants.USER_AGE
import com.example.ubicomp.Utils.Constants.USER_SEX
import com.example.ubicomp.Utils.Permissions
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions


/*  DONE
*   Import permissions to the manifest
*   import implementations of google gms in gradle
*
* */
// PREREQUISITES
// TODO Request Permissions
// PROJECT TASKS
// TODO Create UI
// TODO Switch services
// TODO Broadcast receivers
// TODO 1) ACTION_SCREEN_ON / ACTION_SCREEN_OFF
// TODO Service Class
// TODO 2) Google places API
// TODO RetroFit
// TODO 3) Activity Recognition API
// TODO SQLite
// TODO Send stored data
// TODO Machine Learning
// TODO Notifications
// TODO Shared Preferences
// TODO Graph for stats /*https://www.youtube.com/watch?v=vhKtbECeazQ&ab_channel=ChiragKachhadiya*/

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var userSex: String = ""
    private var validAge = false
    private var userAge = -1

    // Keep user-app state data for each app launch. e.g. services state, user age and sex, data submission etc.
    private lateinit var userPreferences: SharedPreferences
    //private var intent: Intent
    //private lateinit var mARClient: ActivityRecognitionClient;

    // https://developer.android.com/training/location
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load application previous states
        userPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Check if user has submitted personal info
        if (!getStatus()) {
            btn_submit.isEnabled = false
            LocationServiceSwitch.isEnabled = false
            ActivityServiceSwitch.isEnabled = false

            // TODO Comment out!!!!!!!!!!!!!!!!!!!!!!!!
            /* Grab UI Elements and set logic
            * */
            // User age Edit Text
            user_age.addTextChangedListener(textWatcher)
            user_age.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    hideKeyboard(this, v)
                    if (validAge) {
                        tv_error_msg.text = ""
                    } else {
                        tv_error_msg.text = "Λάθος Ηλικία! Επιτρεπτές ηλικές απο 5 έως 60"
                        tv_error_msg.setTextColor(Color.RED)
                    }
                }
                checkIfClickable()
            }

            // User Sex AutoCompleteTextView listener via array list adapter
            val sexOptions = resources.getStringArray(R.array.sexOption)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sexOptions)
            act_user_sex.setAdapter(adapter)
            act_user_sex.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
                userSex = adapter.getItem(pos).toString()
                checkIfClickable()
            }
            act_user_sex.setOnFocusChangeListener { v, focus ->
                if (!focus) {
                    hideKeyboard(this, v)
                    checkIfClickable()
                }
            }

            // Submission button
            btn_submit.setOnClickListener { v ->
                if (checkIfClickable()) {
                    // TODO Save users info
                    savePreferences()
                    disableInput()
                    hideKeyboard(this, v)
                    Toast.makeText(this, "Age: $userAge, Sex: $userSex", Toast.LENGTH_LONG).show()
                    // If submitted hide button, show message change elements appearance and mutability
                }
            }
        } else {
            // If the user has already submit personal info he can't change the data
            disableInput()
            userAge = userPreferences.getInt(USER_AGE, 0)
            userSex = userPreferences.getString(USER_SEX, "").toString()
            user_age.setText(userAge.toString())
            act_user_sex.setText(userSex)
        }

        // Location Service Enable/Disable Switch Button
        LocationServiceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Check multiple permissions
                if (!Permissions.hasFineLocationPermission(this)) {
                    LocationServiceSwitch.isChecked = false
                    //Permissions.requestAllPermissions(this)
                    requestForLocationPermissions()
                } else {
                    enableLocationServices()
                }
            } else {
                disableLocationServices()
            }
        }
        // activity Recognition Service Enable/Disable Switch Button
        ActivityServiceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Check multiple permissions
                if (!Permissions.hasActivityRecognitionPermission(this)) {
                    ActivityServiceSwitch.isChecked = false
                    Permissions.requestActivityRecognitionPermission(this)
                } else {
                    enableActivityService()
                }
            } else {
                disableActivityService()
            }
        }

        barChart.isEnabled = true
        barChart.description.isEnabled = true
        barChart.setDrawBarShadow(true)
        barChart.setDrawValueAboveBar(true)
        barChart.setFitBars(true)
        barChart.setBackgroundColor(Color.GRAY)
        // barChart.data = barChart.generateBarData(1, 20000, 12);

        //mARClient =  ActivityRecognition.getClient(this)
        //intent = Intent(this, ScreenOnOffService::class.java)
        //startService(intent)
    }


    private fun checkIfClickable(): Boolean {
        Log.i("MainActivity", "checkIfClickable()")
        return if (validAge && userSex.isNotBlank()) {
            btn_submit.isEnabled = true
            // TODO In order to keep this, i have to use Share Preferences to keep data after restart.
            // TODO If user data submitted, disappear button and enable services switches.
            ActivityServiceSwitch.isEnabled = true
            LocationServiceSwitch.isEnabled = true
            true
        } else {
            btn_submit.isEnabled = false
            ActivityServiceSwitch.isEnabled = false
            LocationServiceSwitch.isEnabled = false
            false
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        @SuppressLint("UseValueOf", "SetTextI18n")
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.isNotEmpty() == true && !s.contains(".") && !s.contains(",") && Integer.parseInt(
                    s.toString()
                ) in 5 until 60
            ) {
                tv_error_msg.text = "Αποδεκτή Ηλικία"
                tv_error_msg.setTextColor(Color.GREEN)
                userAge = Integer.parseInt(s.toString())
                validAge = true
                checkIfClickable()
            } else {
                tv_error_msg.text = "Λάθος Ηλικία! Επιτρεπτές ηλικές απο 5 έως 60"
                tv_error_msg.setTextColor(Color.RED)
                validAge = false
                checkIfClickable()
            }
        }
    }

    private fun hideKeyboard(ctx: Context, v: View) {
        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun requestForLocationPermissions() {
        EasyPermissions.requestPermissions(
            this,
            "Πρέπει να δώσετε άδεια τοποθεσίας για να λειτουργήσει αυτή η δυνατότητα!",
            Constants.FINE_LOCATION_PERMISSION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.i("MainActivity", "onPermissionsGranted:" + requestCode + ":" + perms.size)
        Log.i("MainActivity", "onPermissionsGranted(cont):$perms")
        if (requestCode == Constants.FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            LocationServiceSwitch.isChecked = true
            enableLocationServices()
        } else if (requestCode == Constants.ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE) {
            ActivityServiceSwitch.isChecked = true
            enableActivityService()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.i("MainActivity", "onPermissionsDenied:" + requestCode + ":" + perms.size)
        Log.i("MainActivity", "onPermissionsDenied(cont):$perms")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //AppSettingsDialog.Builder(this).setRequestCode(requestCode).build().show()
            /*AppSettingsDialog.Builder(this)
                .setTitle("Title")
                .setPositiveButton("Yes")
                .setNegativeButton("No")
                .setRequestCode(requestCode)
                .build()
                .show()*/
        } else {
            //requestForLocationPermissions()
            Permissions.requestAllPermissions(this)
        }
    }

    private fun enableLocationServices() {
        Toast.makeText(this, "Location Service Enabled", Toast.LENGTH_LONG).show()
    }

    private fun disableLocationServices() {
        Toast.makeText(this, "Location Service Disabled", Toast.LENGTH_LONG).show()
    }

    private fun enableActivityService() {
        Toast.makeText(this, "Activity Service Enabled", Toast.LENGTH_LONG).show()
    }

    private fun disableActivityService() {
        Toast.makeText(this, "Activity Service Disabled", Toast.LENGTH_LONG).show()
    }

    private fun disableInput() {
        user_age.isEnabled = false
        act_user_sex.isEnabled = false
        btn_submit.isEnabled = false
    }

    private fun savePreferences() {
        userPreferences.edit()
            .putBoolean(SUBMITTED, true)
            .putInt(USER_AGE, userAge)
            .putString(USER_SEX, userSex)
            .apply()
    }

    private fun getStatus(): Boolean {
        return userPreferences.getBoolean(SUBMITTED, false)
    }
}
