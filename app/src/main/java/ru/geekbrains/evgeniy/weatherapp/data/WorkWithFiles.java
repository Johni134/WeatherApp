package ru.geekbrains.evgeniy.weatherapp.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import ru.geekbrains.evgeniy.weatherapp.R;


public class WorkWithFiles {

    public static boolean fileExist(Context context, String filename) {
        File dir = new File(Objects.requireNonNull(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)).getPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, filename);
        return file.exists();
    }

    public static void saveBitmapToFile(Context context, String filename, Bitmap bmp) {
        if (!isExternalStorageWritable()) {
            showToast(context, context.getText(R.string.toast_external_storage_not_found));
            return;
        }

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap loadBitmapFromFile(Context context, String filename) {
        if (!isExternalStorageReadable()) {
            showToast(context, context.getText(R.string.toast_external_storage_not_found));
            return null;
        }

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), filename);

        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }

        return null;
    }

    // проверим доступен ли external storage для записи/чтения
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // проверим доступен ли external storage для чтения
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
               Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static void showToast(Context context, CharSequence toastMessage) {
        Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT);
        toast.show();
    }
}
