package com.nixbug.entebus.presentation.screens.ticket.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.nixbug.entebus.presentation.screens.ticket.Passenger

@Composable
fun PassengerCountList(passengers: List<Passenger>, onCountChange: (Passenger, Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(8.dp, 10.dp)
        ) {
            // Passenger items
            items(passengers) { passenger ->
                PassengerCountItem(
                    passenger = passenger,
                    onCountChange = onCountChange
                )
            }
        }
    }
}

@Composable
fun PassengerCountItem(passenger: Passenger, onCountChange: (Passenger, Int) -> Unit) {
    val textValue = if (passenger.passengerCount == 0) "" else passenger.passengerCount.toString()
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .background(
                color = Color.White, shape = RoundedCornerShape(12.dp)
            )
            .padding(5.dp, 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // Passenger name
            Text(
                text = passenger.name,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
            )

            // Counter row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                CounterButton(text = "−", onClick = {
                    if (passenger.passengerCount > 0) {
                        onCountChange(passenger, passenger.passengerCount - 1)
                    }
                })
                BasicTextField(
                    value = textValue,
                    onValueChange = { input ->
                        if (input.isDigitsOnly()) {
                            onCountChange(
                                passenger,
                                input.toIntOrNull() ?: 0
                            )
                        }
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isFocused && textValue.isEmpty()) {
                                Text(
                                    text = "0",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .width(46.dp)
                        .height(38.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(Color(0xFFF9F9F9))
                        .onFocusChanged {
                            isFocused = it.isFocused
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                )
                CounterButton(
                    text = "+",
                    onClick = {
                        onCountChange(passenger, passenger.passengerCount + 1)
                    })
            }
        }
    }
}

@Composable
fun CounterButton(
    text: String, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(
                color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {

        Text(
            text = text, color = Color.White, style = MaterialTheme.typography.titleLarge
        )
    }
}
