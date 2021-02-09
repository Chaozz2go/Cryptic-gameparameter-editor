package com.github.tristan_11.gameparameteryaml.model;

import com.github.tristan_11.gameparameteryaml.Baum;
import com.jfoenix.controls.JFXTreeView;
import javafx.scene.control.TreeItem;

import java.util.*;

/**
 * Handler für den TreeView. Bastelt alles um den TreeView und holt sich oder bekommt die entsprechenden Sachen.
 */
public class TreeViewHandler {
    Baum baum;
    JFXTreeView treeView;

    /**
     * Konstruktor. Setzt die gegebenen Werte im Objekt.
     *
     * @param treeView Der TreeView, der gehandled wird.
     */
    public TreeViewHandler(Map<String, Object> map, JFXTreeView<?> treeView) {
        this.treeView = treeView;

        this.baum = new Baum();
        mapToBaum(map, baum);
    }


    /**
     * Setzt den Tree. Bekommt den Filter übergeben und holt sich die Daten aus der Yaml.
     *
     * @param filter Filtertext, nach dem im TreeView gesucht wird.
     */
    public void setData(String filter) {

        TreeItem<String> rootItem;
        Baum filteredBaum = applyFilter(baum, filter);
        if (filteredBaum==null) {
            rootItem = new TreeItem<>("");
        }
        else {
            rootItem = baumToTreeItem(filteredBaum);
        }

        //Setzt den Filter für die leaves
        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);
    }

    private TreeItem<String> baumToTreeItem(Baum baum){
        TreeItem item = new TreeItem(baum.getName());

        for (Baum child : baum.getChildren()) {
            item.getChildren().add(baumToTreeItem(child));
        }
        return item;
    }

    private Baum applyFilter(Baum baum, String filter) {

        if(baum.getName().contains(filter)){
            return baum;
        }

        HashSet<Baum> appliedChildren = new HashSet<>();
        Baum returner = null;
        for (Baum child : baum.getChildren()) {
            returner = applyFilter(child, filter);
            if (returner !=null) {
                appliedChildren.add(returner);
            }
        }
        if(appliedChildren.isEmpty()){
            return null;
        }
        returner = new Baum();
        returner.setName(baum.getName());
        returner.addChildren(appliedChildren);
        return returner;
    }


    /**
     * Bekommt die map aus der yaml übergeben und baut rekursiv bis auf variablen level das TreeItem runter.
     */
    private void mapToBaum(Map<String, Object> map, Baum baum) {
        map.forEach((s, o) -> {
            Baum treeItem = new Baum();
            treeItem.setName(s);
            if (o instanceof Map) {
                this.mapToBaum((Map<String, Object>) o, treeItem);
            }
            baum.getChildren().add(treeItem);
        });
    }

    /**
     * Gibt eine Liste<String> mit den Parents von 1stLevel->leave zurück. (Exklusive Root)
     *
     * @return {@link List} pathElements
     */
    public ArrayList<String> getPathToItem() {
        ArrayList<String> pathElements = new ArrayList<>();
        for (TreeItem item = (TreeItem) this.treeView.getSelectionModel().getSelectedItem();
             item != null; item = (TreeItem) item.getParent()) {
            pathElements.add(0, item.getValue().toString());
        }
        if(!pathElements.isEmpty()) {
            pathElements.remove(0);
        }
        return pathElements;
    }
}
