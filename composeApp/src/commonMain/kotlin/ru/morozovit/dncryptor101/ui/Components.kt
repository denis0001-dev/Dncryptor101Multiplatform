package ru.morozovit.dncryptor101.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ru.morozovit.compat.left
import ru.morozovit.compat.link
import ru.morozovit.compat.right

data class NavigationItem(
    val name: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector = selectedIcon,
    val onClick: () -> Unit
)

@Composable
fun SimpleNavigationBar(
    selectedItem: Int,
    vararg items: NavigationItem,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector =
                        if (selectedItem == index)
                            items[index].selectedIcon
                        else
                            items[index].unselectedIcon,
                        contentDescription = item.name
                    )
                },
                label = { Text(item.name) },
                selected = selectedItem == index,
                onClick = items[index].onClick
            )
        }
    }
}

@Composable
fun SimpleNavigationRail(
    selectedItem: Int,
    vararg items: NavigationItem,
    modifier: Modifier = Modifier,
) {
    NavigationRail(modifier) {
        items.forEachIndexed { index, item ->
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector =
                        if (selectedItem == index)
                            items[index].selectedIcon
                        else
                            items[index].unselectedIcon,
                        contentDescription = item.name
                    )
                },
                label = { Text(item.name) },
                selected = selectedItem == index,
                onClick = items[index].onClick
            )
        }
    }
}

@Composable
inline fun Step(
    number: Int,
    title: String,
    supportingText: String,
    modifier: Modifier = Modifier,
    isOptional: Boolean = false,
    crossinline content: @Composable () -> Unit
) {
    val stepColor = if (isOptional)
        ListItemDefaults.colors().supportingTextColor
    else
        LocalContentColor.current
    ConstraintLayout(modifier) {
        val (stepNum, titleText, supportingTextText, content1) = createRefs()
        Box(
            Modifier
                .padding(end = 10.dp)
                .border(
                    2.dp,
                    stepColor,
                    CircleShape
                )
                .size(30.dp)
                .constrainAs(stepNum) {
                    top link parent.top
                    left link parent.left
                },
            contentAlignment = Center
        ) {
            Text(
                text = "$number",
                color = stepColor,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(titleText) {
                top link stepNum.top
                left link stepNum.right
                bottom link stepNum.bottom
                right link parent.right
                width = Dimension.fillToConstraints
            },
            color = stepColor,
        )
        Text(
            text = supportingText,
            color = ListItemDefaults.colors().supportingTextColor,
            modifier = Modifier
                .constrainAs(supportingTextText) {
                    top link titleText.bottom
                    left link stepNum.right
                    right link parent.right
                    width = Dimension.fillToConstraints
                }
                .padding(top = 5.dp),
        )
        Box(
            modifier = Modifier
                .constrainAs(content1) {
                    top link supportingTextText.bottom
                    left link stepNum.right
                    right link parent.right
                    width = Dimension.fillToConstraints
                }
                .padding(top = 10.dp)
        ) {
            content()
        }
    }
}