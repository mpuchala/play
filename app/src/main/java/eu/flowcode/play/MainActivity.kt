package eu.flowcode.play

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import eu.flowcode.play.ui.theme.PlayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       /* lifecycleScope.launchWhenStarted {
            viewModel.flow.collect {
                log("Flow: $it")
            }
        }*/
        lifecycleScope.launchWhenStarted {
            viewModel.counter.collect {
                log("Counter State: $it")
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.counterShared.collect {
                log("Counter Shared: $it")
            }
        }

        setContent {
            PlayTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Playground(viewModel)
                }
            }
        }
    }

    private fun log(value: String) {
        Log.e("Test", value)
    }
}

@Composable
fun Playground(viewModel: MainViewModel) {
    val counterValueState = viewModel.counter.collectAsState()

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Counter: ${counterValueState.value}")
        TextButton(onClick = { viewModel.incrementCounter() }) {
            Text(text = "Increment Please!")
        }

        Spacer(modifier = Modifier.width(20.dp))

        Text("Flow: ")
        TextButton(onClick = { viewModel.onClickManageFlow() }) {
            Text(text = "Manage Flow")
        }

        TextButton(onClick = { viewModel.onClickRx() }) {
            Text(text = "Start rx")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlayTheme {
        //Playground(4, {})
    }
}