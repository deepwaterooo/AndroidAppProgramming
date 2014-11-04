package dev.drawingfun;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.UUID;
import android.util.Log;
import android.graphics.Color;
import android.support.v4.app.*;

import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Toast;

//public class MainActivity extends Activity implements OnClickListener {
//public class MainActivity extends FragmentActivity implements OnAmbilWarnaListener {
public class MainActivity extends FragmentActivity implements OnAmbilWarnaListener, View.OnClickListener {
    
    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, colorBtn, undoBtn, redoBtn;
    private float smallBrush, mediumBrush, largeBrush;
    private static int mColor = 0; // mColor, colorBtn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = (DrawingView)findViewById(R.id.drawing);

        // get rid of this layout already
        //LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        // get the first button and store it as the instance variable
        //currPaint = (ImageButton)paintLayout.getChildAt(0);
        // show the button is selected
        //currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);
        drawView.setBrushSize(mediumBrush);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        colorBtn = (ImageButton)findViewById(R.id.color_btn);
        //colorBtn.setOnClickListener(this);
        colorBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {                
                    showColorPicker();
                }
            });
        // when activity is re-created, we need to set OnAmbilWarnaListen
        if (savedInstanceState != null) {
            AmbilWarnaDialogFragment fragment = (AmbilWarnaDialogFragment)getSupportFragmentManager().findFragmentByTag("color_picker_dialog");
            if (fragment != null) {
                fragment.setOnAmbilWarnaListener(this);
            }
        }
        
        undoBtn = (ImageButton)findViewById(R.id.undo_btn);
        //undoBtn.setOnClickListener(this);
        undoBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {                
                    drawView.onClickUndo();
                }
            });
        
        redoBtn = (ImageButton)findViewById(R.id.redo_btn);
        //redoBtn.setOnClickListener(this);
        redoBtn.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {                
                    drawView.onClickRedo();
                }
            });
    }

    @Override
    public void onClick(View view) {
        // respond to clicks -- for drawButton
        if (view.getId() == R.id.draw_btn) {
            // draw button clicked
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Brush size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            // for small brush size
            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
            // for medium brush size
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
            // for large brush size
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
            drawView.setErase(false);
            brushDialog.show();
        } else if (view.getId() == R.id.erase_btn) {
            // respond to clicks -- for erase button
            final Dialog brushDialog = new Dialog(this);
            brushDialog.setTitle("Erase size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            // for three different size buttons
            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
            brushDialog.show();
        } else if (view.getId() == R.id.new_btn) {
            // new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
            newDialog.show();
        } else if (view.getId() == R.id.save_btn) {
            drawView.setDrawingCacheEnabled(true);
            // save drawing
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        // save drawing
                    }
                });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
            saveDialog.show();

            // attemp to write the image to a file
            String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(),
                                                                  UUID.randomUUID().toString()+".png", "drawing");
            // give user feedback
            if (imgSaved != null){
                Toast savedToast = Toast.makeText(getApplicationContext(), "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            } else {
                Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }
            // destroy the drawing cache so that any future drawings saved won't use the existing cache
            drawView.destroyDrawingCache();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*
    public void paintClicked(View view) {
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        // uer chosen color
        if (view != currPaint){
            // update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);

            // update UI to reflect the new chosen paint and set the previous one back to normal
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton)view;
        }
    }
    */
    @Override
    public void onCancel(AmbilWarnaDialogFragment dialogFragment)  {
        Log.d("TAG", "onCancel()");
    }
    
    @Override
    public void onOk(AmbilWarnaDialogFragment dialogFragment, int color)  {
        Log.d("TAG", "onOk(). Color: " + color);
        //MainActivity.this.mColor = color;

        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());

        // uer chosen color
        //if (view != currPaint) {
        if (color != mColor) {
            // update color
            //ImageButton imgView = (ImageButton)view;
            //String color = view.getTag().toString();
            MainActivity.this.mColor = color;
            drawView.setColor(mColor);
            // update UI to reflect the new chosen paint and set the previous one back to normal
            //imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            //currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            //currPaint = (ImageButton)view;
        }
    }

    // show Color Picker dialog fragment. If color wasn't set previously, set BLUE by default
    private void showColorPicker() {
        int thisColor = mColor == 0 ? Color.BLUE : mColor;
        // create new instance of AmbilWarnaDialogFragment and set OnAmbilWarnaListener to it
        // show dialog fragment with some tag value
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AmbilWarnaDialogFragment fragment = AmbilWarnaDialogFragment.newInstance(thisColor, android.R.style.Theme_Dialog);
        fragment.setOnAmbilWarnaListener(this);
        fragment.show(ft, "color_picker_dialog");
    }
}

/*
  } else if(view.getId() == R.id.color_btn){
  final int initialColor = 100;

  // create OnAmbilWarnaListener instance
  // new color can be retrieved in onOk() event
  OnAmbilWarnaListener onAmbilWarnaListener = new OnAmbilWarnaListener() {
  @Override
  public void onCancel(AmbilWarnaDialogFragment dialogFragment) {
  Log.d("TAG", "onCancel()");
  }
  @Override
  public void onOk(AmbilWarnaDialogFragment dialogFragment, int color) {
  Log.d("TAG", "onOk().color: " + color);
  //String color = view.getTag().toString();
  //drawView.setColor(color);
  MainActivity.this.mColor = color;
  }
  };
            
  // create new instance of AmbilWarnaDialogFragment and set OnAmbilWarnaListener listener to it
  // show dialog fragment with some tag value
  FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
  AmbilWarnaDialogFragment fragment = AmbilWarnaDialogFragment.newInstance(mColor);
  fragment.setOnAmbilWarnaListener(onAmbilWarnaListener);
  fragment.show(ft, "color_picker_dialog");
            
  // select the color Dialog
  /*
  ColorWheelDialog dialog = new ColorWheelDialog(this, initialColor, new OnAmbilWarnaListener() {
  @Override
  public void onOK(ColorWheelDialog dialog, int color) {
  // color is the color selected by the user.
  // dialog.onOK(dialog, color);
  }  
  @Override
  public void onCancel(ColorWheelDialog dialog) {
  // cancel was selected by the user
  // colorGot = initialColor;
  }
  });  
  dialog.show();*/
