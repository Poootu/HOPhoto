@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hophoto

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hophoto.ui.theme.HOPhotoTheme
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.hophoto.copiedTestCode.test1
import com.example.hophoto.copiedTestCode.test2


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        var lastImage : Bitmap = Bitmap.createBitmap(1,2,Bitmap.Config.ARGB_8888)
        super.onCreate(savedInstanceState)
        if(!hasPermission())
        {
            ActivityCompat.requestPermissions(this, CAMERAX_PERMISSIONS,0)
        }
        enableEdgeToEdge()
        setContent {
        var showImage by remember{ mutableStateOf(false) }
            HOPhotoTheme {

                val scaffoldState = rememberBottomSheetScaffoldState()
                val camController = remember {
                    LifecycleCameraController(applicationContext).apply{ //localContext.current is similar i think
                        setEnabledUseCases(
                            CameraController.IMAGE_ANALYSIS
                            or CameraController.IMAGE_CAPTURE
                        )
                    }
                }
                    BottomSheetScaffold(
                        sheetContent = {
                            if (showImage) {
                                Image(
                                    bitmap = lastImage.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        },
                        scaffoldState = scaffoldState,
                        sheetPeekHeight = 10.dp
                    ) { paddingValues ->
                        Box(modifier = Modifier.fillMaxSize().padding(paddingValues))
                        {
                            CameraPreview(
                                camController = camController, modifier = Modifier.fillMaxSize()
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            if (showImage) {
                                Image(
                                    bitmap = lastImage.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            IconButton(
                                onClick = {
                                    takePhoto(camController,
                                        { imgBitmap ->


                                            val newBitmap = Bitmap.createScaledBitmap(imgBitmap,512,512,false)
                                            val array: IntArray =
                                                IntArray(newBitmap.height * newBitmap.width)
                                            newBitmap.asImageBitmap().readPixels(
                                                array,
                                                0,
                                                0,
                                                width = newBitmap.width,
                                                height = newBitmap.height,
                                                0
                                            )
                                            newBitmap.setPixels(
                                            test2(
                                                array,
                                                newBitmap.height,
                                                newBitmap.width
                                            ),
                                            0,
                                            newBitmap.width,
                                            0,
                                            0,
                                            newBitmap.width,
                                            newBitmap.height
                                            )
                                            Log.i(
                                                "test",
                                                "width ${newBitmap.width}  height ${newBitmap.height}"
                                            )
                                            lastImage = newBitmap
                                            showImage = true
                                            Log.i("test",showImage.toString())
                                        })
                                },
                                modifier = Modifier.offset(10.dp, 10.dp).weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Take photo"
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun takePhoto(camController: LifecycleCameraController, onPhotoTaken: (Bitmap) -> Unit) {
        camController.takePicture(ContextCompat.getMainExecutor(applicationContext),
            object : OnImageCapturedCallback()
            {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    onPhotoTaken(image.toBitmap())
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                }

            }
            )
    }

    private fun hasPermission() : Boolean
    {
        return CAMERAX_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object
    {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HOPhotoTheme {
        Greeting("Android")
    }
}