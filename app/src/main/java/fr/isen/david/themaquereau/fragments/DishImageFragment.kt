package fr.isen.david.themaquereau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import fr.isen.david.themaquereau.R
import fr.isen.david.themaquereau.databinding.FragmentDishImageBinding

// Instances of this class are fragments representing a single
// object in the list of images.
class DishImageFragment : Fragment() {
    private lateinit var binding: FragmentDishImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishImageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            // get image link from parent fragment
            val imageLink = getString(ARG_OBJECT)
            // Image
            renderImage(imageLink)
        }
    }

    private fun renderImage(imageLink: String?) {
        val picasso = Picasso.get()
        if (!imageLink.isNullOrEmpty()) {
            // image link is found
            picasso
                .load(imageLink)
                .into(binding.dishPagedImage)
        } else {
            // by default
            picasso
                .load(R.drawable.maquereau_not_found)
                .into(binding.dishPagedImage)
        }
    }

    companion object {
        const val ARG_OBJECT = "object"
    }
}