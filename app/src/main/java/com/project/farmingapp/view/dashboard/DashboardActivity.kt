package com.project.farmingapp.view.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.project.farmingapp.R
import com.project.farmingapp.databinding.ActivityDashboardBinding
import com.project.farmingapp.view.apmc.ApmcFragment
import com.project.farmingapp.view.articles.ArticleListFragment
import com.project.farmingapp.view.auth.LoginActivity
import com.project.farmingapp.view.ecommerce.EcommerceFragment
import com.project.farmingapp.view.ecommerce.MyOrdersFragment
import com.project.farmingapp.view.introscreen.IntroActivity
import com.project.farmingapp.view.socialmedia.SMCreatePostFragment
import com.project.farmingapp.view.socialmedia.SocialMediaPostsFragment
import com.project.farmingapp.view.user.UserFragment
import com.project.farmingapp.view.weather.WeatherFragment
import com.project.farmingapp.viewmodel.UserDataViewModel
import com.project.farmingapp.viewmodel.WeatherViewModel

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var weatherViewModel: WeatherViewModel // RESTORED
    private lateinit var userDataViewModel: UserDataViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient // RESTORED
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String> // RESTORED
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDataViewModel = ViewModelProvider(this)[UserDataViewModel::class.java]
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java] // RESTORED

        setupToolbarAndDrawer()
        setupBottomNavigation()
        setupLocationServices()
        getLocation() // <--- ADD THIS LINE to automatically fetch location on startup.
        handleAppLaunch()
        observeUserData()

        if (savedInstanceState == null) {
            Log.d("DashboardActivity", "Setting initial DashboardFragment...")
            setCurrentFragment(DashboardFragment())
        }
    }

    private fun setupToolbarAndDrawer() {
        setSupportActionBar(binding.appBarDashboard.toolbar)
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarDashboard.toolbar,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bottomNavAPMC -> setCurrentFragment(ApmcFragment())
                R.id.bottomNavHome -> setCurrentFragment(DashboardFragment())
                R.id.bottomNavEcomm -> setCurrentFragment(EcommerceFragment())
                R.id.bottomNavPost -> setCurrentFragment(SocialMediaPostsFragment())
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        Log.d("DashboardActivity", "Fragment being set: ${fragment.javaClass.simpleName}")
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content_frame, fragment)
            .commit()
    }

    private fun handleAppLaunch() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("firstTime", true)) {
            startActivity(Intent(this, IntroActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        } else if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
    }

    private fun observeUserData() {
        firebaseAuth.currentUser?.email?.let { userDataViewModel.getUserData(it) }
        val navHeader = binding.navView.getHeaderView(0)
        navHeader.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            setCurrentFragment(UserFragment())
        }
        userDataViewModel.userLiveData.observe(this) { snapshot: DocumentSnapshot? ->
            snapshot?.let {
                navHeader.findViewById<TextView>(R.id.navbarUserName).text = it.getString("name")
                navHeader.findViewById<TextView>(R.id.navbarUserEmail).text = firebaseAuth.currentUser?.email
                Glide.with(this).load(it.getString("profileImage"))
                    .into(navHeader.findViewById(R.id.navbarUserImage))
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miItem1 -> setCurrentFragment(EcommerceFragment())
            R.id.miItem2 -> setCurrentFragment(ApmcFragment())
            R.id.miItem3 -> setCurrentFragment(SMCreatePostFragment())
            R.id.miItem4 -> setCurrentFragment(SocialMediaPostsFragment())
            R.id.miItem5 -> setCurrentFragment(WeatherFragment())
            R.id.miItem6 -> setCurrentFragment(ArticleListFragment())
            R.id.miItem7 -> setCurrentFragment(MyOrdersFragment())
            R.id.miItem8 -> showLogoutDialog()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this).setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                firebaseAuth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // RESTORED: All your location logic is back
    private fun setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    getLocation()
                } else {
                    Toast.makeText(this, "Permission Denied. Weather features will be limited.", Toast.LENGTH_SHORT).show()
                }
            }
        // You can now request permission whenever needed, for example:
        // requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location for weather updates.", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // THIS IS THE FIX: Call the one, correct function.
                    weatherViewModel.fetchWeatherData(location.latitude, location.longitude)
                    Toast.makeText(this, "Fetching weather...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Could not get location. Retrying.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}