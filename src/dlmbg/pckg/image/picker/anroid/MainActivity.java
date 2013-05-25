package dlmbg.pckg.image.picker.anroid;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/*
* Gede Lumbung - 2013
* http://gedelumbung.com
* Simple Image Picker from SD Card and Camera
*/

public class MainActivity extends Activity {
	
	private Uri UrlGambar;
	private ImageView SetImageView;	
	
	private static final int CAMERA = 1;
	private static final int FILE = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        final String [] pilih			= new String [] {"Camera", "SD Card"};				
		ArrayAdapter<String> arr_adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,pilih);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);
		
		builder.setTitle("Pilih Gambar");
		builder.setAdapter( arr_adapter, new DialogInterface.OnClickListener() 
		{
			public void onClick( DialogInterface dialog, int pilihan ) 
			{
				if (pilihan == 0) 
				{
					Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File file		 = new File(Environment.getExternalStorageDirectory(),
							   			"image_picker/img_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
					UrlGambar = Uri.fromFile(file);

					try {			
						intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, UrlGambar);
						intent.putExtra("return-data", true);
						
						startActivityForResult(intent, CAMERA);
					} catch (Exception e) {
						e.printStackTrace();
					}			
					
					dialog.cancel();
				} 
				else if(pilihan == 1) 
				{
					Intent intent = new Intent();
					
	                intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                
	                startActivityForResult(Intent.createChooser(intent, "Pilih Aplikasi"), FILE);
				}
			}
		} );
		
		final AlertDialog dialog = builder.create();
		
		SetImageView = (ImageView) findViewById(R.id.img_set);
		
		Button tmb_pilih = (Button) findViewById(R.id.btn_pilih);
		tmb_pilih.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	   
		Bitmap bitmap 	= null;
		String path		= "";
		
		if (requestCode == FILE) 
		{
			UrlGambar = data.getData(); 
			path = getRealPath(UrlGambar);
		
			if (path == null)
			{
				path = UrlGambar.getPath();
			}
			else
			{
				bitmap 	= BitmapFactory.decodeFile(path);
			}
		} 
		else 
		{
			path	= UrlGambar.getPath();
			bitmap  = BitmapFactory.decodeFile(path);
		}

		Toast.makeText(this, path,Toast.LENGTH_LONG).show();
		SetImageView.setImageBitmap(bitmap);		
	}
	
    public String getRealPath(Uri contentUri) 
    {
        String path = null;
        String[] images_data = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, images_data, null, null, null);
        if(cursor.moveToFirst())
        {
           int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
           path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }
}