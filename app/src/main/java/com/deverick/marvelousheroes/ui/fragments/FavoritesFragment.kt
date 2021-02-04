package com.deverick.marvelousheroes.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.deverick.marvelousheroes.viewmodels.FavoritesViewModel
import com.deverick.marvelousheroes.databinding.FavoritesFragmentBinding
import com.deverick.marvelousheroes.ui.adapters.FavoritesAdapter
import com.deverick.marvelousheroes.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private lateinit var binding: FavoritesFragmentBinding
    private lateinit var favoritesAdapter: FavoritesAdapter
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FavoritesFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setupRecyclerView()

        favoritesAdapter.setOnItemClickListener {
            findNavController().navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(it)
            )
        }

        viewModel.characters.observe(viewLifecycleOwner, { res ->
            when (res) {
                is Resource.Success -> {
                    hideLoading()
                    favoritesAdapter.differ.submitList(res.data)
                }

                is Resource.Loading -> {
                    showLoading()
                }

                is Resource.Error -> {
                    hideLoading()
                    Snackbar.make(view, "Error", Snackbar.LENGTH_SHORT).show()
                }

                else -> {
                    Snackbar.make(view, "No favorites found", Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun hideLoading() {
        binding.progressBar.visibility = INVISIBLE
    }

    private fun showLoading() {
        binding.progressBar.visibility = VISIBLE
    }

    private fun setupToolbar() {
        val navController = findNavController()

        binding.detailsToolbar.setupWithNavController(navController)
        binding.detailsToolbar.title = null
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter()

        binding.rvFavorites.apply {
            adapter = favoritesAdapter
            layoutManager = GridLayoutManager(activity, 2)
        }
    }
}