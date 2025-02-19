package cat.jan.fileprovider2

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import cat.jan.fileprovider2.databinding.ActivityMainBinding
import java.io.File
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnTakeFoto.setOnClickListener()
        {
            //startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE),)
            //Fent servir File Provider ara haurem de gestionar millor el retorn de l'Intent de la Càmera
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
                it.resolveActivity(packageManager).also{component->
                    //File pot ser un fitxer emmagatzemat a la memòria, no cal que estigui al magatzem del dispositiu
                    //val photoFile:File

                    //Crearem un métode que guardi el File que necessitem

                    createPhotoFile()

                    //Uri sí que queda emmagatzemat a una ruta del magatzem del dispositiu
                    val photoUri: Uri = FileProvider.getUriForFile(this,"cat.jan.fileprovider2.fileprovider2", file)

                    it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    //Hem reanomenat l'iterador per defecte a component per poder continuar tinguen accés a l'iterador it que fa referència a l'intent. Sinó no ens deixaria
                }
            }
            //Ara cridarem el launch passant el l'intent modificat
            startForResult.launch(intent)
            //also vol dir que sobre aquest intent també farem més coses(also)

        }
    }
    //Creem una variable global perquè file el necessitarem a més d'un lloc.

    private lateinit var file:File

    private fun createPhotoFile() {
        //Necessitem accedir a un directori extern
        //Enviroment.DIRECTORY_PICTURES retorna la ruta on es guarden les images al dispositiu
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        //Crearem un fitxer temporal
        //El nom del fitxer serà "IMG_" seguit del temps actual en milisegons acabat en _. Ho indiquem al prefix:
        //L'extensió l'indicarem al "sufix" i serà -jpg

        file = File.createTempFile("Jan_ " + Calendar.getInstance().time + "_",".png", dir)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK)
        {
            val intent = result.data
            //Ara el btmap es troba enregistrat al File ara
            //val imageBitmap = intent?.extras?.get("data") as Bitmap
            val imageBitmap = BitmapFactory.decodeFile(file.toString())
            val imageView = binding.miniatureFoto
            imageView.setImageBitmap(imageBitmap)
        }
    }
}