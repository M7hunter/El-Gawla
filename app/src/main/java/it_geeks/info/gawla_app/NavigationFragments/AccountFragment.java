package it_geeks.info.gawla_app.NavigationFragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import de.hdodenhof.circleimageview.CircleImageView;
import it_geeks.info.gawla_app.General.PicassoClint;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.MainActivity;
import it_geeks.info.gawla_app.Models.Data;
import it_geeks.info.gawla_app.Models.Request;
import it_geeks.info.gawla_app.Models.RequestMainBody;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.RESTful.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {

    String name,image;
    TextView userName;
    CircleImageView userImage;
    ImageView edit_user_image,edit_user_image_upload;
    int user_id;
    String api_token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        getData();

        initViews(view);

        setData();

        return view;
    }

    private void getData() {  /// get data from sharedPrafrence
       name = SharedPrefManager.getInstance(getContext()).getUser().getName();
       image = SharedPrefManager.getInstance(getContext()).getUserImage();
       user_id = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();
       api_token = SharedPrefManager.getInstance(getContext()).getUser().getApi_token();
    }

    private void initViews(View v) {  //  initialize Views
        userName = v.findViewById(R.id.user_name);
        userImage = v.findViewById(R.id.user_image);
        edit_user_image = v.findViewById(R.id.edit_user_image);
        edit_user_image_upload = v.findViewById(R.id.edit_user_image_upload);
    }

    private void setData() { // set data to views
        PicassoClint.downloadImage(MainActivity.mainActivityInstance,image,userImage);
        userName.setText(name);
        edit_user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoGalary();
            }
        });
    }

    private void gotoGalary() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i,"Select Picture"),1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK) {
            Uri selecteImage = data.getData();

            if (selecteImage != null) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selecteImage);
                    userImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] byteArray = outputStream.toByteArray();

                final String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                edit_user_image_upload.setVisibility(View.VISIBLE);
                edit_user_image_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         UploadImage(encodedImage);
                    }
                });

            }
        }
    }

    private void UploadImage(String encodedImage ) {
        edit_user_image_upload.setVisibility(View.INVISIBLE);
        final RequestMainBody requestMainBody = new RequestMainBody(new Data("updateUserDate"),new Request(user_id,api_token,encodedImage));
        try {
            new AsyncUploadImage().execute(requestMainBody);
        }catch (RuntimeException e){ }

    }

      private class AsyncUploadImage extends AsyncTask<RequestMainBody,Void,String>{


          @Override
          protected String doInBackground(RequestMainBody... requestMainBody) {
              final String[] imageuploaded = new String[1];
              Call<JsonObject> call = RetrofitClient.getInstance().getAPI().UploadImage(requestMainBody[0]);
              call.enqueue(new Callback<JsonObject>() {
                  @Override
                  public void onResponse(Call<JsonObject> call,final Response<JsonObject> response) {

                              JsonObject ObjData = response.body().getAsJsonObject();
                              boolean status = ObjData.get("status").getAsBoolean();
                              if (status){
                                  JsonObject data = ObjData.get("userData").getAsJsonObject();
                                  imageuploaded[0] = data.get("image").getAsString();
                                  SharedPrefManager.getInstance(getActivity()).saveUserImage(image);
                                  Toast.makeText(MainActivity.mainActivityInstance, "Your Profile Image has been changed", Toast.LENGTH_SHORT).show();
                                  PicassoClint.downloadImage(MainActivity.mainActivityInstance,imageuploaded[0],userImage);
                              }else{
                                  Toast.makeText(MainActivity.mainActivityInstance, handleServerErrors(ObjData), Toast.LENGTH_SHORT).show();
                              }

                  }

                  @Override
                  public void onFailure(Call<JsonObject> call, Throwable t) {
                      Toast.makeText(MainActivity.mainActivityInstance, t.getMessage()+"", Toast.LENGTH_SHORT).show();
                  }
              });
              return imageuploaded[0];
          }

          @Override
          protected void onPostExecute(String image) {
              super.onPostExecute(image);
          }

          @Override
          protected void onProgressUpdate(Void... values) {
              super.onProgressUpdate(values);
          }
      }

    private String handleServerErrors(JsonObject object) {  // get Response Error
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
    }


}// end of class
