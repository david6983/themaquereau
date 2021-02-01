package fr.isen.david.themaquereau.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import fr.isen.david.themaquereau.ARG_OBJECT
import fr.isen.david.themaquereau.R
import fr.isen.david.themaquereau.adapters.DishImageAdapter
import fr.isen.david.themaquereau.model.domain.Item

class DishImagesPagerFragment : Fragment() {
    // When requested, this adapter returns a DishImageFragment,
    // representing an object in the collection.
    private lateinit var dishDetailsImageAdapter: DishImageAdapter
    private lateinit var viewPager: ViewPager2
    private var item: Item = Item()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            getSerializable(ARG_OBJECT)?.let { serializedItem ->
                item = serializedItem as Item
            }
        }
        return inflater.inflate(R.layout.fragment_dish_images_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fragment = this
        // Retrieve the item from the detail activity
        dishDetailsImageAdapter = DishImageAdapter(fragment, item)
        viewPager = view.findViewById(R.id.dishDetailImagesPager)
        viewPager.adapter = dishDetailsImageAdapter
    }

    companion object {
        val TAG = DishImagesPagerFragment::class.java.simpleName
    }
}