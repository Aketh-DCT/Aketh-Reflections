package gr.aketh.echoes.classes

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import org.apache.commons.net.ftp.FTPClient
import java.io.FileOutputStream
import java.lang.Exception

class FtpClientConnect(applicationContext: Context){
    private val ftpServer = "21717280951.thesite.link"
    private val ftpPort = 21

    private val defaultFolderLocations = "/game_content"


    private var FTP_USERNAME: String = "playandlearn"
    private var FTP_PASSWORD: String = "c870&7hYl"



    private val ftpClientS = FTPClient()

    private var applicationContext: Context

    init{

        this.applicationContext = applicationContext


        //FTP_USERNAME = AppConfig.getUsername()
        //FTP_PASSWORD = AppConfig.getPassword()
    }

    suspend fun start(){
        withContext(Dispatchers.IO) {
            ftpClientS.connect(ftpServer)
            ftpClientS.login(FTP_USERNAME, FTP_PASSWORD)
            ftpClientS.changeWorkingDirectory(defaultFolderLocations)

            val localDirectory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                applicationContext.getExternalFilesDir(null)
            } else {
                Environment.getExternalStorageDirectory()
            }

            //This will run if it can find the directory
            localDirectory?.let { downloadFiles(it, "") }

            ftpClientS.disconnect()
        }
    }


    fun downloadFiles(localDirectory: File, remoteDirectory: String)
    {

       // val files = ftpClientS.listFiles("/test")
        val files = ftpClientS.listFiles(remoteDirectory)
        for(file in files)
        {
            if(file.isDirectory){
                //This is a directory, create a local directory with the same name but first check if it exists
                val localSubDirectory = File(localDirectory, file.name)
                if(!localSubDirectory.exists())
                {
                    localSubDirectory.mkdir()
                }
                Log.d("FILENAME",file.name)
                //Recursively download the files in this derectory
                downloadFiles(localSubDirectory, "$remoteDirectory/${file.name}")
            }
            else{
                //This is a file, check if it is already downloaded
                Log.d("FILENAME",file.name)
                val localFile = File(localDirectory, file.name)
                if(!localFile.exists() || localFile.length() != file.size){
                    // The file is not downloaded or the downloaded file is different from the remote file
                    val outputStream = FileOutputStream(localFile)
                    ftpClientS.retrieveFile("$remoteDirectory/${file.name}", outputStream)
                    outputStream.close()
                }
            }
        }
    }




}