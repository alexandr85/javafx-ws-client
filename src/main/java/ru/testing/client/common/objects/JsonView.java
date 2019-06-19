package ru.testing.client.common.objects;

import com.google.gson.*;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.controlsfx.control.SegmentedButton;

import java.util.List;

public class JsonView {

    private boolean parseStatus;
    private TreeItem<String> tree;

    public JsonView(String jsonString) {
        try {
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(jsonString);
            parseStatus = true;
            tree = createTree(root);
        } catch (JsonSyntaxException e) {
            parseStatus = false;
        }
    }

    public static TreeCell<String> cellFactory(TreeView<String> tv) {
        return new TreeCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item);
                }
            }
        };
    }

    private static void expandJsonTree(TreeItem<String> item, boolean value) {
        item.expandedProperty().setValue(value);
        List<TreeItem<String>> items = item.getChildren();
        if (items.size() > 0) {
            items.forEach(i -> JsonView.expandJsonTree(i, value));
        }
    }

    public void apply(TreeView<String> jsonView, ToggleButton bJsonPretty, SegmentedButton segmentedButton) {
        if (parseStatus) {
            jsonView.setRoot(tree);
            JsonView.expandJsonTree(jsonView.getRoot(), true);
        } else {
            bJsonPretty.setDisable(true);
            Tooltip tp = new Tooltip("json is invalid");
            tp.setShowDelay(new Duration(100));
            segmentedButton.setTooltip(tp);
        }
    }

    private TreeItem<String> createTree(JsonElement element) {
        if (element.isJsonNull()) {
            return new TreeItem<>("null");
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            return new TreeItem<>(
                    primitive.isString() ? '"' + primitive.getAsString() + '"' : primitive.getAsString()
            );
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            TreeItem<String> item = new TreeItem<>("[ ]");

            array.forEach(c -> {
                TreeItem<String> child = createTree(c);
                item.getChildren().add(child);
            });

            return item;
        } else {
            JsonObject object = element.getAsJsonObject();
            TreeItem<String> item = new TreeItem<>("{ }");

            object.entrySet().forEach(o -> {
                TreeItem<String> child = createTree(o.getValue());
                prependItem(child, o.getKey());
                item.getChildren().add(child);
            });

            return item;
        }
    }

    private void prependItem(TreeItem<String> item, String key) {
        String value = item.getValue();
        item.setValue(value == null ? String.format("\"%s\"", key) : String.format("\"%s\": %s", key, value));
    }
}
