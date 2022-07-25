package com.fruitfal.arjunsbi.core.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/** UI Related */

fun Fragment.showToast(message: String) =
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

fun Fragment.showSnackBar(message: String) =
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()