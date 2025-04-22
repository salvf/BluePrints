/*
 * Copyright 2019 Salvador Vera Franco.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package blueprints.ui;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author Salvador Vera Franco
 */
public class BPNode<T> implements Node<T> {

    private T value;
    private boolean connection = false;
    private final Class<T> datatype;
    private final boolean isInput;
    private Ellipse2D.Double gnode;
    private final ArrayList<OutputNodeListener> outlisteners = new ArrayList<>();
    private final ArrayList<InputNodeListener> inlisteners = new ArrayList<>();
    private final ArrayList<ConnectionListener> connectionlisteners = new ArrayList<>();
    private double x, y;
    private final int size = 10;
    private BPComponent parent;

    public BPNode(Class<T> datatype, boolean isInput) {
        this.isInput = isInput;
        this.datatype = datatype;
    }

    @Override
    public void setValue(T value) {
        if (equalsType(value)) {
            T oldvalue = this.value;
            this.value = value;
            if (!isInput)
                outlisteners.forEach((l) -> {
                    l.OnChange(this, oldvalue, this.value);
                });
            else
                inlisteners.forEach((l) -> {
                    l.OnRecive(this, this.value);
                });
        } else
            throw new NodeTypeException("The value of this node is of different type than the value entered");
    }

    @Override
    public T getValue() {
        return this.value;
    }

    public String getType() {
        return this.datatype.getSimpleName();
    }

    public void setPosition(double x, double y) {
        setX(x);
        setY(y);
    }

    public double getX() {
        return this.x;
    }

    public void setParent(BPComponent component) {
        this.parent = component;
    }

    public BPComponent getParent() {
        return this.parent;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Point2D getDirectionVector() {
        if (isInput)
            return new Point2D.Double(-70, y );
        else
            return new Point2D.Double(70, y);
    }

    public boolean addInputNodeListener(NodeAdapter listener) {
        return inlisteners.add(listener);
    }

    public boolean addOutputNodeListener(OutputNodeListener listener) {
        return outlisteners.add(listener);
    }

    public boolean addConnetionListener(ConnectionListener listener) {
        return connectionlisteners.add(listener);
    }

    public boolean removeInputNodeListener(NodeAdapter listener) {
        return inlisteners.remove(listener);
    }

    public boolean removeOutputNodeListener(OutputNodeListener listener) {
        return outlisteners.remove(listener);
    }

    public boolean removeConnetionListener(ConnectionListener listener) {
        return connectionlisteners.remove(listener);
    }

    public Ellipse2D.Double setGraphNode(Ellipse2D.Double gnode) {
        return this.gnode = gnode;
    }

    public Ellipse2D.Double getGraphNode() {
        return this.gnode;
    }

    public void setConnection(boolean conn, BPNode node) {
        this.connection = conn;
        if (isConnected())
            if (isInput())
                connectionlisteners.forEach(cnsmr -> {
                    cnsmr.onConnect(node, this);
                });
            else
                connectionlisteners.forEach(cnsmr -> {
                    cnsmr.onConnect(this, node);
                });
        else if (isInput())
            connectionlisteners.forEach(cnsmr -> {
                cnsmr.onDisconnect(node, this);
            });
        else
            connectionlisteners.forEach(cnsmr -> {
                cnsmr.onDisconnect(this, node);
            });

    }

    public boolean isConnected() {
        return this.connection;
    }

    public boolean isInput() {
        return this.isInput;
    }

    private boolean equalsType(T value) {
        return value.getClass().getSimpleName().toLowerCase().contains(
                getType().toLowerCase());
    }

    public void draw(Graphics2D g2d) {
        g2d.fill(setGraphNode(centeredNode(x, y, size)));
    }

    private Ellipse2D.Double centeredNode(double x, double y, int r) {
        x = x - (r / 2);
        y = y - (r / 2);
        return new Ellipse2D.Double(x, y, r, r);
    }

    public boolean contains(int mx, int my) {
        return Math.sqrt(Math.pow(mx - x, 2) + Math.pow(my - y, 2)) <= size / 2;
    }

    // Check if this node can connect to another node
    public boolean canConnectTo(BPNode other) {
        // Cannot connect to the same type (INPUT-INPUT or OUTPUT-OUTPUT)
        if (isInput == other.isInput && other.getParent().equals(this.getParent())) {
            return false;
        }

        // Data types must match
        if (!getType().equals(other.getType())) {
            return false;
        }

        return true;
    }
}