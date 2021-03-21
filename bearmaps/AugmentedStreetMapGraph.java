package bearmaps;

import bearmaps.utils.Constants;
import bearmaps.utils.graph.streetmap.Node;
import bearmaps.utils.graph.streetmap.StreetMapGraph;
import bearmaps.utils.ps.KDTree;
import bearmaps.utils.ps.Point;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {
    //instances
    private List<Point> streetpoints;
    private KDTree kdTree;
    private Map<Point, Long> PointMap; // key -- point of the node, value -- vertex id.
    private MyTrieSet allNodesName;
    private Map<Long, Node> idToNode;

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        streetpoints = new ArrayList<>();
        PointMap = new HashMap<>();
        allNodesName = new MyTrieSet();
        idToNode = new HashMap<>();
        // You might find it helpful to uncomment the line below:
         List<Node> nodes = this.getAllNodes();
         for(Node node: nodes){
             Long id = node.id();
             double x = projectToX(node.lon(), node.lat());
             double y = projectToY(node.lon(), node.lat());
             Point point = new Point(x, y);
             if(isNavigableNode(node)){
                 streetpoints.add(point);
             }
             if(node.name() != null){
                 String name = cleanString(node.name());
                 allNodesName.add(name, id);
                 idToNode.put(id, node);
             }
             PointMap.put(point, id);
         }
        kdTree = new KDTree(streetpoints);
    }


    /**
     * For Project Part III
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        double x = projectToX(lon, lat);
        double y = projectToY(lon, lat);
        Point closest = kdTree.nearest(x,y);
        return PointMap.get(closest);
    }

    /**
     * Return the Euclidean x-value for some point, p, in Berkeley. Found by computing the
     * Transverse Mercator projection centered at Berkeley.
     * @param lon The longitude for p.
     * @param lat The latitude for p.
     * @return The flattened, Euclidean x-value for p.
     * @source https://en.wikipedia.org/wiki/Transverse_Mercator_projection
     */
    static double projectToX(double lon, double lat) {
        double dlon = Math.toRadians(lon - ROOT_LON);
        double phi = Math.toRadians(lat);
        double b = Math.sin(dlon) * Math.cos(phi);
        return (K0 / 2) * Math.log((1 + b) / (1 - b));
    }

    /**
     * Return the Euclidean y-value for some point, p, in Berkeley. Found by computing the
     * Transverse Mercator projection centered at Berkeley.
     * @param lon The longitude for p.
     * @param lat The latitude for p.
     * @return The flattened, Euclidean y-value for p.
     * @source https://en.wikipedia.org/wiki/Transverse_Mercator_projection
     */
    static double projectToY(double lon, double lat) {
        double dlon = Math.toRadians(lon - ROOT_LON);
        double phi = Math.toRadians(lat);
        double con = Math.atan(Math.tan(phi) / Math.cos(dlon));
        return K0 * (con - Math.toRadians(ROOT_LAT));
    }


    /**
     * For Project Part IV (extra credit)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        String cleaned_prefix = cleanString(prefix);
        List<Long> idList = this.allNodesName.keysWithPrefix(cleaned_prefix);
        List<String> result = new ArrayList<>();
        if(idList != null) {
            for (Long i : idList) {
                result.add(idToNode.get(i).name());
            }
        }
        return result;
    }

    /**
     * For Project Part IV (extra credit)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        List<Long> idList = this.allNodesName.exactName(cleanString(locationName));
        if(idList.isEmpty()){
            return null;
        }else {
            List<Map<String, Object>> result = new ArrayList<>();
            for (Long s : idList){
                Map<String, Object> currInfo = new HashMap<>();
                Node curr = idToNode.get(s);
                currInfo.put("lat", curr.lat());
                currInfo.put("lon", curr.lon());
                currInfo.put("name", idToNode.get(s).name());
                currInfo.put("id", s);
                result.add(currInfo);
            }
            return result;
        }
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

        
    /**
     * Scale factor at the natural origin, Berkeley. Prefer to use 1 instead of 0.9996 as in UTM.
     * @source https://gis.stackexchange.com/a/7298
     */
    private static final double K0 = 1.0;
    /** Latitude centered on Berkeley. */
    private static final double ROOT_LAT = (Constants.ROOT_ULLAT + Constants.ROOT_LRLAT) / 2;
    /** Longitude centered on Berkeley. */
    private static final double ROOT_LON = (Constants.ROOT_ULLON + Constants.ROOT_LRLON) / 2;

}
