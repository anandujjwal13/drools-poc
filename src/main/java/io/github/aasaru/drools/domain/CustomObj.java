package io.github.aasaru.drools.domain;

import org.json.JSONObject;

public class CustomObj {
    JSONObject obj;

    public CustomObj(JSONObject obj) {
        this.obj = obj;
    }

    public Boolean hasField(String field){
        return this.obj.has(field) ;
    }



    public int getIntValue(String field) {
        if(this.obj.has(field)){
            return (int) this.obj.get(field);
        }

        return 0;

    }

    public String getStringValue(String field) {

        if(this.obj.has(field)){

            return (String) this.obj.get(field);
        }

        return " ";

    }
}
