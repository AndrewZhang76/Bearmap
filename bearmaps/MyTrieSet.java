package bearmaps;

import bearmaps.utils.graph.streetmap.StreetMapGraph;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyTrieSet{
    public TrieSetNode emptyRoot;

    public MyTrieSet(){
        this.emptyRoot = new TrieSetNode(false);
    }

    public void clear(){
        this.emptyRoot.chars.clear();
    }

    public boolean contains(String key){
        TrieSetNode curr = this.emptyRoot;
        for(int i = 0; i < key.length(); i++){
            char elem = key.charAt(i);
            if(curr.chars.containsKey(elem)){
                curr = curr.chars.get(elem);
            } else{
                return false;
            }
        }
        if(curr.flag){
            return true;
        }
        return false;
    }

    public void add(String key, Long id){
        TrieSetNode curr = this.emptyRoot;
        for(int i = 0; i < key.length(); i++){
            char elem = key.charAt(i);
            if(!curr.chars.containsKey(elem)){
                curr.chars.put(elem, new TrieSetNode(false));
            }
            curr = curr.chars.get(elem);
        }
        if(curr.ids == null) {
            curr.ids = new ArrayList<>();
        }
        curr.ids.add(id);
        curr.flag = true;
    }

    public static void main(String[] args){
        MyTrieSet newTrie = new MyTrieSet();
        newTrie.add("aileen", 114514L);
        newTrie.add("ael", 191810L);
        newTrie.add("aileen", 810893L);
        newTrie.add("ai", 871351L);

        List<Long> result = newTrie.keysWithPrefix("ai");
        for (Long i:result) {
            System.out.println(i);
        }}

    public List<Long> keysWithPrefix(String prefix){
        List<String> result = new ArrayList<>();
        TrieSetNode curr = this.emptyRoot;
        List toReturn = new ArrayList();
        for(int i = 0; i < prefix.length(); i++){
            char elem = prefix.charAt(i);
            if(curr.chars.containsKey(elem)) {
                curr = curr.chars.get(elem);
            } else {
                return toReturn;
            }
        }
        prefixHelper(toReturn, curr);
        return toReturn;
    }

    public List<Long> exactName(String name){
        TrieSetNode curr = this.emptyRoot;
        List toReturn = new ArrayList();
        for(int i = 0; i < name.length(); i++){
            char elem = name.charAt(i);
            if(curr.chars.containsKey(elem)) {
                curr = curr.chars.get(elem);
            } else {
                return toReturn;
            }
        }
        if(curr.flag){
            return curr.ids;
        } else {
            return toReturn;
        }
    }

    public static void prefixHelper(List<Long> lst, TrieSetNode node){
        if(node.flag){
            for(Long id: node.ids){
                lst.add(id);
            }
        }
        for(char i: node.chars.keySet()){
            prefixHelper(lst, node.chars.get(i));
        }
    }

    public String longestPrefixOf(String key){
        throw new UnsupportedOperationException();
    }

    private class TrieSetNode{
        public boolean flag;
        public HashMap<Character, TrieSetNode> chars = new HashMap<>();
        public ArrayList<Long> ids;

        public TrieSetNode(boolean flag){
            this.flag = flag;
        }

    }

}
