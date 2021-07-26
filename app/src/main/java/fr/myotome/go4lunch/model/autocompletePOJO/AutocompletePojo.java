// TODO MYOTOME jamais de maj dans un package (certaines lib ou même Java dans certains cas n'aiment pas du tout ça et ça produit des bugs chelous
package fr.myotome.go4lunch.model.autocompletePOJO;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AutocompletePojo {

    @SerializedName("predictions")
    @Expose
    private List<Prediction> predictions = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Prediction> getPredictions() {
        return predictions;
    }

    // TODO MYOTOME de façon générale, considère toujours tes POJOs comme des objets immutables, donc pas de setters et les fields en final
    // TODO MYOTOME @Nullable sur tous les fields aussi c'est toujours intéressant
    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
