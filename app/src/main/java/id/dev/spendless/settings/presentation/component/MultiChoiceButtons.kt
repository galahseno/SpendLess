package id.dev.spendless.settings.presentation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleSegmentedButtonsCreator(list: List<String>, selectedIndex: Int) {

    var currentSelectedIndex by remember { mutableIntStateOf(selectedIndex) }



    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(buttonBackground.copy(0.2f))
            .padding(horizontal = 2.dp)
    ) {


        list.forEachIndexed { index, label ->
            SegmentedButton(
                selected = false,
                onClick = {
                    currentSelectedIndex = index
                },
                enabled = currentSelectedIndex != index,
                shape = RoundedCornerShape(12.dp),
                label = { Text(label) },
                colors = SegmentedButtonDefaults.colors(
                    inactiveContainerColor = Color.Transparent,
                    inactiveBorderColor = Color.Transparent,
                    disabledInactiveContainerColor = Color.White,
                    disabledInactiveBorderColor = Color.White
                ),
                modifier = Modifier.padding(
                    start = if (index == 0) 2.dp else 0.dp,
                    end = if (index == list.lastIndex) 2.dp else 0.dp
                )
            )

        }

    }


}

@Preview(showBackground = true)
@Composable
private fun ButtonPreview() {

    SpendLessTheme {
        val list = listOf("ONE", "TWO", "THREE",)
        MultipleSegmentedButtonsCreator(list, 2)
    }

}