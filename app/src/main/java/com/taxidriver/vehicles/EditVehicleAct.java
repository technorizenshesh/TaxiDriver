package com.taxidriver.vehicles;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.taxidriver.R;
import com.taxidriver.adapters.AdapterMakeVehicle;
import com.taxidriver.adapters.AdapterModelVehicle;
import com.taxidriver.databinding.ActivityEditVehicleBinding;
import com.taxidriver.models.ModelCarsType;
import com.taxidriver.models.ModelLogin;
import com.taxidriver.models.ModelMake;
import com.taxidriver.models.ModelVehicle;
import com.taxidriver.utils.AppConstant;
import com.taxidriver.utils.Compress;
import com.taxidriver.utils.ProjectUtil;
import com.taxidriver.utils.SharedPref;
import com.taxidriver.utils.retrofitutils.Api;
import com.taxidriver.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditVehicleAct extends AppCompatActivity {

    private static final int PERMISSION_ID = 101;
    Context mContext = EditVehicleAct.this;
    ActivityEditVehicleBinding binding;
    private final int GALLERY = 0, CAMERA = 1;
    File vehicleImage;
    SharedPref sharedPref;
    String type;
    boolean isMakeRun, isModelRun;
    ModelLogin modelLogin;
    ModelVehicle.Result result;
    private String str_image_path, isBasic, isNormal, isLuxurious, isPool;
    ArrayList<String> taxiNamesList = new ArrayList<>();
    ArrayList<String> taxiIdsList = new ArrayList<>();
    ArrayList<String> makeNameList = new ArrayList<>();
    ArrayList<String> makeIdList = new ArrayList<>();
    ArrayList<String> modelNameList = new ArrayList<>();
    ArrayList<String> modelIdList = new ArrayList<>();
    private String carId = "", makeId = "", modelId = "", makeIdEdit = "", modelIdEdit = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_vehicle);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        result = (ModelVehicle.Result) getIntent().getSerializableExtra("data");

        makeId = result.getBrand();
        modelId = result.getCar_model();

        Log.e("makeIdmakeId", "ModelVehicle = " + new Gson().toJson(result));
        Log.e("makeIdmakeId", "result Make Name = " + result.getMake_name());
        Log.e("makeIdmakeId", "result Model Name = " + result.getCar_model());

        itit();

    }

    private void itit() {

        getMake();

        binding.spYear.setEnabled(false);
        binding.spMakeType.setEnabled(false);
        binding.tvSpMake.setEnabled(false);
        binding.tvSpModel.setEnabled(false);
        binding.spModelType.setEnabled(false);
        binding.etNumberPlate.setEnabled(false);
        binding.cbBasic.setEnabled(false);
        binding.cbNormal.setEnabled(false);
        binding.cbPool.setEnabled(false);
        binding.cbLuxurious.setEnabled(false);

        binding.tvSpMake.setText(result.getMake_name());
        binding.tvSpModel.setText(result.getModel_name());

        binding.etNumberPlate.setText(result.getCar_number());

        for (int i = 0; i < binding.spYear.getCount(); i++) {
            if (result.getYear_of_manufacture().equals(binding.spYear.getItemAtPosition(i))) {
                binding.spYear.setSelection(i);
            }
        }

        if ("Yes".equals(result.getBasic_car())) {
            isBasic = result.getBasic_car();
            binding.cbBasic.setChecked(true);
        }

        if ("Yes".equals(result.getLuxurious_car())) {
            isLuxurious = result.getLuxurious_car();
            binding.cbLuxurious.setChecked(true);
        }

        if ("Yes".equals(result.getPool_car())) {
            isPool = result.getPool_car();
            binding.cbPool.setChecked(true);
        }

        if ("Yes".equals(result.getNormal_car())) {
            isNormal = result.getNormal_car();
            binding.cbNormal.setChecked(true);
        }

        binding.tvSpMake.setOnClickListener(v -> {
            binding.spMakeType.performClick();
        });

        binding.tvSpModel.setOnClickListener(v -> {
            binding.spModelType.performClick();
        });

        binding.spMakeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    makeIdEdit = makeIdList.get(position);
                    binding.tvSpMake.setText(makeNameList.get(position));
                    // getModels(makeIdEdit);
                    Log.e("getCarsgetCars", "makeId = " + modelId);
                } catch (Exception e) {
                    Log.e("ExceptionException", "Exception = " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        binding.spModelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    modelIdEdit = modelIdList.get(position);
                    binding.tvSpModel.setText(modelNameList.get(position));
                    Log.e("getCarsgetCars", "modelId = " + modelId);
                } catch (Exception e) {
                    Log.e("ExceptionException", "Exception = " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        binding.spinnerServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    carId = taxiIdsList.get(position);
                    Log.e("getCarsgetCars", "carId = " + carId);
                } catch (Exception e) {
                    Log.e("ExceptionException", "Exception = " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        binding.ivUploadImage.setOnClickListener(v -> {
            if (checkPermissions()) {
                showPictureDialog();
            } else {
                requestPermissions();
            }
        });

        binding.btnUpdate.setOnClickListener(v -> {

            Log.e("asdasdasdas", "model Id = " + modelId);
            Log.e("asdasdasdas", "make Id = " + makeId);

            if (carId == null) {
                Toast.makeText(mContext, getString(R.string.select_vehicle_type), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etNumberPlate.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_number_plate), Toast.LENGTH_SHORT).show();
            } else if (binding.spYear.getSelectedItemPosition() == 0) {
                Toast.makeText(mContext, getString(R.string.add_year_vehicle_text), Toast.LENGTH_SHORT).show();
            } else {

                if (binding.cbBasic.isChecked()) {
                    isBasic = "Yes";
                } else {
                    isBasic = "No";
                }

                if (binding.cbNormal.isChecked()) {
                    isNormal = "Yes";
                } else {
                    isNormal = "No";
                }

                if (binding.cbLuxurious.isChecked()) {
                    isLuxurious = "Yes";
                } else {
                    isLuxurious = "No";
                }

                if (binding.cbPool.isChecked()) {
                    isPool = "Yes";
                } else {
                    isPool = "No";
                }

                if (binding.cbBasic.isChecked()) {
                    editVehicle();
                } else if (binding.cbNormal.isChecked()) {
                    editVehicle();
                } else if (binding.cbLuxurious.isChecked()) {
                    editVehicle();
                } else if (binding.cbPool.isChecked()) {
                    editVehicle();
                } else {
                    Toast.makeText(mContext, getString(R.string.please_select_service_type), Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    private void editVehicle() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        MultipartBody.Part vehicleFilePart;

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody carType = RequestBody.create(MediaType.parse("text/plain"), carId);
        RequestBody carBrand = RequestBody.create(MediaType.parse("text/plain"), makeId);
        RequestBody carModel = RequestBody.create(MediaType.parse("text/plain"), modelId);
        RequestBody vehicleId = RequestBody.create(MediaType.parse("text/plain"), result.getId());
        RequestBody year = RequestBody.create(MediaType.parse("text/plain"), binding.spYear.getSelectedItem().toString().trim());
        RequestBody carNumber = RequestBody.create(MediaType.parse("text/plain"), binding.etNumberPlate.getText().toString().trim());
        RequestBody basic_car = RequestBody.create(MediaType.parse("text/plain"), isBasic);
        RequestBody normal_car = RequestBody.create(MediaType.parse("text/plain"), isNormal);
        RequestBody luxurious_car = RequestBody.create(MediaType.parse("text/plain"), isLuxurious);
        RequestBody pool_car = RequestBody.create(MediaType.parse("text/plain"), isPool);

        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
        vehicleFilePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        // vehicleFilePart = MultipartBody.Part.createFormData("car_image", "", RequestBody.create(MediaType.parse("car_document/*"), vehicleImage));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.editDriverVehicle(userId, carBrand, carModel,
                carNumber, year, basic_car, normal_car, luxurious_car, pool_car, vehicleId, vehicleFilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("vehicleData", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        finish();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }

        });

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, PERMISSION_ID);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ProjectUtil.openGallery(mContext, GALLERY);
                                break;
                            case 1:
                                str_image_path = ProjectUtil.openCamera(mContext, CAMERA);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = ProjectUtil.getRealPathFromURI(mContext, data.getData());
                Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        vehicleImage = file;
                        binding.ivUploadImage.setImageURI(Uri.parse(file.getPath()));
                    }
                }).CompressedImage(path);
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        vehicleImage = file;
                        binding.ivUploadImage.setImageURI(Uri.parse(file.getPath()));
                    }
                }).CompressedImage(str_image_path);
            }
        }

    }

    private void getCars() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCarList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            ModelCarsType modelCarsType = new Gson().fromJson(stringResponse, ModelCarsType.class);
                            for (ModelCarsType.Result item : modelCarsType.getResult()) {
                                taxiNamesList.add(item.getCar_name());
                                taxiIdsList.add(item.getId());
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
                                    R.layout.support_simple_spinner_dropdown_item, taxiNamesList);
                            binding.spinnerServiceType.setAdapter(arrayAdapter);
                            carId = taxiNamesList.get(0);
                            Log.e("getCarsgetCars", "response = " + stringResponse);
                            Log.e("getCarsgetCars", "carId = " + carId);
                        } else {
                            Toast.makeText(mContext, getString(R.string.no_cat_found), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void getMakeNew() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCarMakeList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {

                            ModelMake modelCarsType = new Gson().fromJson(stringResponse, ModelMake.class);

                            for (ModelMake.Result item : modelCarsType.getResult()) {
                                makeNameList.add(item.getTitle());
                                makeIdList.add(item.getId());
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
                                    R.layout.support_simple_spinner_dropdown_item, makeNameList);
                            binding.spMakeType.setAdapter(arrayAdapter);

                            for (int i = 0; i < modelNameList.size(); i++) {
                                if (modelNameList.get(i).equals(result.getModel_name())) {
                                    Log.e("modelIdmodelId", "modelNameList Name= " + modelNameList.get(i));
                                    Log.e("modelIdmodelId", "make Id = " + makeIdList.get(i));
                                    modelId = modelIdList.get(i);
                                    binding.spMakeType.setSelection(i);
                                }
                            }

                            Log.e("getMake", "response getMake = " + stringResponse);
                            Log.e("getMake", "makeId getMake = " + makeId);

                        } else {
                            Toast.makeText(mContext, getString(R.string.no_make_found), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void getMake() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCarMakeList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {

                            ModelMake modelMake = new Gson().fromJson(stringResponse, ModelMake.class);

                            AdapterMakeVehicle adapterMakeVehicle = new AdapterMakeVehicle(mContext, modelMake.getResult());
                            binding.spMakeType.setAdapter(adapterMakeVehicle);

                            getModeNew(makeId);

                            Log.e("getMake", "response getMake = " + stringResponse);
                            Log.e("getMake", "makeId getMake = " + makeId);

                        } else {
                            Toast.makeText(mContext, getString(R.string.no_make_found), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void getModeNew(String makeId){
        Log.e("makeIdmakeId", "Make Id for Model = " + makeId);
        // ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("make_id", makeId);
        Log.e("makeIdmakeId", "Make Id for Model = " + makeId);
        Call<ResponseBody> call = api.getCarModelCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            ModelMake modelModel = new Gson().fromJson(stringResponse, ModelMake.class);

                            AdapterModelVehicle adapterMakeVehicle = new AdapterModelVehicle(mContext, modelModel.getResult());
                            binding.spModelType.setAdapter(adapterMakeVehicle);

                        } else {
                            Toast.makeText(mContext, getString(R.string.no_models_found), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void getModels(String makeId) {
        Log.e("makeIdmakeId", "Make Id for Model = " + makeId);
        // ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("make_id", makeId);
        Log.e("makeIdmakeId", "Make Id for Model = " + makeId);
        Call<ResponseBody> call = api.getCarModelCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            ModelMake modelModel = new Gson().fromJson(stringResponse, ModelMake.class);

                            AdapterModelVehicle adapterMakeVehicle = new AdapterModelVehicle(mContext, modelModel.getResult());
                            binding.spModelType.setAdapter(adapterMakeVehicle);

                            binding.tvSpModel.setText(modelModel.getResult().get(0).getTitle());
                            modelId = modelModel.getResult().get(0).getId();

                            //                            modelNameList = new ArrayList<>();
//                            modelIdList = new ArrayList<>();
//                            for (ModelMake.Result item : modelCarsType.getResult()) {
//                                modelNameList.add(item.getTitle());
//                                modelIdList.add(item.getId());
//                            }
//                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
//                                    R.layout.support_simple_spinner_dropdown_item, modelNameList);
//                            binding.spModelType.setAdapter(arrayAdapter);
                            // modelId = modelIdList.get(0);

                            Log.e("getModels", "response = " + stringResponse);
                            Log.e("getModels", "modelId = " + modelId);

                        } else {
                            Toast.makeText(mContext, getString(R.string.no_models_found), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    public void getMakeId(String makeIdClicked, String title) {
        makeId = makeIdClicked;
        binding.tvSpMake.setText(title);
        ProjectUtil.hideSpinnerDropDown(binding.spMakeType);
        getModels(makeId);
        Log.e("fsafasfasfdas", "makeId = " + makeId);
    }

    public void getModelId(String modelIdClicked, String title) {
        modelId = modelIdClicked;
        binding.tvSpModel.setText(title);
        ProjectUtil.hideSpinnerDropDown(binding.spModelType);
        Log.e("fsafasfasfdas", "modelId = " + modelId);
    }

    private void getModelsNew() {
        Log.e("makeIdmakeId", "Make Id for Model = " + makeId);
        // ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("make_id", makeId);
        Call<ResponseBody> call = api.getCarModelCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            ModelMake modelCarsType = new Gson().fromJson(stringResponse, ModelMake.class);

                            modelNameList = new ArrayList<>();
                            modelIdList = new ArrayList<>();

                            for (ModelMake.Result item : modelCarsType.getResult()) {
                                modelNameList.add(item.getTitle());
                                modelIdList.add(item.getId());
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
                                    R.layout.support_simple_spinner_dropdown_item, modelNameList);
                            binding.spModelType.setAdapter(arrayAdapter);
                            modelId = result.getCar_model();

                            Log.e("getModels", "response = " + stringResponse);
                            Log.e("getModels", "modelId = " + modelId);

                        } else {
                            Toast.makeText(mContext, getString(R.string.no_models_found), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void updateSpinnerModel() {
        isModelRun = true;
        for (int i = 0; i < modelNameList.size(); i++) {
            if (modelNameList.get(i).equals(result.getModel_name())) {
                Log.e("modelIdmodelId", "modelNameList Name= " + modelNameList.get(i));
                Log.e("modelIdmodelId", "make Id = " + makeIdList.get(i));
                makeId = makeIdList.get(i);
                binding.spMakeType.setSelection(i);
            }
        }
    }

    private void updateSpinners() {

        isMakeRun = true;

        if (result.getBasic_car().equals("Yes")) {
            binding.cbBasic.setChecked(true);
        }

        if (result.getNormal_car().equals("Yes")) {
            binding.cbNormal.setChecked(true);
        }

        if (result.getLuxurious_car().equals("Yes")) {
            binding.cbLuxurious.setChecked(true);
        }

        if (result.getPool_car().equals("Yes")) {
            binding.cbPool.setChecked(true);
        }

        binding.etNumberPlate.setText(result.getCar_number());

        String compareValue = result.getYear_of_manufacture();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.select_year, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spYear.setAdapter(adapter);

        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            binding.spYear.setSelection(spinnerPosition);
        }

        for (int i = 0; i < makeNameList.size(); i++) {
            if (makeNameList.get(i).equals(result.getMake_name())) {
                Log.e("makeIdmakeId", "makeNameList Name= " + makeNameList.get(i));
                Log.e("makeIdmakeId", "make Id = " + makeIdList.get(i));
                makeId = makeIdList.get(i);
                binding.spMakeType.setSelection(i);
            }
        }

        Log.e("modelIdmodelId", "modelNameList Name result = " + result.getModel_name());
        Log.e("modelIdmodelId", "model Id result = " + result.getCar_model());
        Log.e("modelIdmodelId", "modelIdList result = " + modelIdList);

    }

}