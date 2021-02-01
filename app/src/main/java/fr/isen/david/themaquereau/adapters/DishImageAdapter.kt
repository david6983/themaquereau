package fr.isen.david.themaquereau.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import fr.isen.david.themaquereau.ARG_OBJECT
import fr.isen.david.themaquereau.fragments.DishImageFragment
import fr.isen.david.themaquereau.fragments.DishImagesPagerFragment
import fr.isen.david.themaquereau.model.domain.Item

class DishImageAdapter(
    fragment: Fragment,
    private val item: Item
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return item.images.size
    }

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = DishImageFragment()
        fragment.arguments = Bundle().apply {
            // Give the image url given the position to the next fragment
            putString(ARG_OBJECT, item.images[position])
        }
        return fragment
    }
}