package com.nhom.movieapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.nhom.movieapp.databinding.ActivityMainBinding

/**
 * Single Activity + Bottom Navigation.
 * Chưa đăng nhập thì mở thẳng màn Login, và ẩn thanh Bottom Navigation ở Login/Register.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        // Chọn màn hình bắt đầu tùy trạng thái đăng nhập.
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        val loggedIn = FirebaseAuth.getInstance().currentUser != null
        graph.setStartDestination(if (loggedIn) R.id.exploreFragment else R.id.loginFragment)
        navController.graph = graph

        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible =
                destination.id != R.id.loginFragment && destination.id != R.id.registerFragment
        }
    }
}
