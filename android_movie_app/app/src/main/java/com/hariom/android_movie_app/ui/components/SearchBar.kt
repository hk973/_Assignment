package com.hariom.android_movie_app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.hariom.android_movie_app.ui.theme.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Search bar component with debounced text changes
 * Implements Material 3 design with search icon and clear button
 * 
 * @param query Current search query
 * @param onQueryChange Callback invoked with debounced search query
 * @param placeholder Placeholder text to display when empty
 * @param modifier Modifier for the search bar
 * @param debounceMillis Debounce delay in milliseconds (default: 300ms)
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    debounceMillis: Long = 300L
) {
    var localQuery by remember { mutableStateOf(query) }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Debounce effect - only trigger onQueryChange after user stops typing
    LaunchedEffect(localQuery) {
        delay(debounceMillis)
        if (localQuery != query) {
            onQueryChange(localQuery)
        }
    }
    
    // Sync external query changes with local state
    LaunchedEffect(query) {
        if (query != localQuery) {
            localQuery = query
        }
    }
    
    OutlinedTextField(
        value = localQuery,
        onValueChange = { localQuery = it },
        placeholder = { 
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSearchPlaceholder
            ) 
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSearchPlaceholder
            )
        },
        trailingIcon = {
            if (localQuery.isNotEmpty()) {
                IconButton(
                    onClick = { 
                        localQuery = ""
                        onQueryChange("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = TextSearchPlaceholder
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SearchBarBorder,
            unfocusedBorderColor = SearchBarBorder,
            focusedContainerColor = SearchBarBackground,
            unfocusedContainerColor = SearchBarBackground,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = PurpleAccent
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
            }
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary)
    )
}