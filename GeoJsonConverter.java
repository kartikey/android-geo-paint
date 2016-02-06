/**
 * Code for converting Polylines into GeoJSON formatted Strings 
 * @author  Kyungmin Lee
 */
public class GeoJsonConverter {

 /**
  * Converts the given list of Polylines into a GeoJSON formatted String 
  */
 public static String convertToGeoJson(List<Polyline> lines) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"type\": \"FeatureCollection\", " +
                "\"features\": [");
        for (Polyline line : lines) {
            builder.append("{ \"type\": \"Feature\", " +
                    "        \"geometry\": { \"type\": \"LineString\", \"coordinates\": ");
            List<LatLng> points = line.getPoints();
            builder.append(points.toString());
            builder.append("}, \"properties\": { \"color\" : \"" + line.getColor() + "\"}");
            builder.append("},");
        }
        // removes the last , at the very end
        builder = builder.deleteCharAt(builder.length() - 1);
        builder.append("]}");
        return builder.toString().replace("lat/lng:", "").replace("(", "[").replace(")", "]");
    }


}