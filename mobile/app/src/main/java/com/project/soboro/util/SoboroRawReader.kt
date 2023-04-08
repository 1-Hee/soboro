package com.project.soboro.util

import android.widget.TextView
import com.project.soboro.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class SoboroRawReader {

    companion object {

        fun setText(textView: TextView, ins:InputStream){
            try {
                val br = BufferedReader(InputStreamReader(ins));

                val stringBuilder = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    stringBuilder.append(line).append("\n");
                }

                textView.text = stringBuilder.toString();
                // Log.d("SOBORO::::::::", "${stringBuilder.toString()}")

            } catch (e: IOException) {
                e.printStackTrace();
            } catch (e : java.lang.NullPointerException){
                e.printStackTrace();
            }
        }

    }
}


