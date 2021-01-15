package com.anchalajay.dinder.utility;

import com.anchalajay.dinder.pojos.PojoMyDog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParser {

    public static ArrayList<PojoMyDog> parseRawJsonData(String rawJson){
        ArrayList<PojoMyDog> randomDogsList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(rawJson);
            JSONArray jsonArray = jsonObject.getJSONArray("message");
            for(int i = 0; i < jsonArray.length(); i++){
                PojoMyDog myDog = new PojoMyDog();
                String url = jsonArray.getString(i);
                myDog.setImage_url(url);
                String[] strs = url.trim().split("/");
                for(int j = 0; j < strs.length; j++){
                    if(strs[j].equalsIgnoreCase("breeds")){
                        myDog.setBreed_name(strs[j+1]);
                        break;
                    }
                }
                randomDogsList.add(myDog);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return randomDogsList;
    }
}
