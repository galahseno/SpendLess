package id.dev.spendless.core.presentation.design_system.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.secondaryColor

@Composable
fun SpendLessFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LargeFloatingActionButton(
        modifier = modifier
            .size(64.dp),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        containerColor = secondaryColor,
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "add_transaction",
            modifier = Modifier.size(28.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SpendLessFabPreview() {
    SpendLessTheme {
        SpendLessFab(
            onClick = {}
        )
    }
}