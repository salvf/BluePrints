# BluePrints

BluePrints is a Java-based visual programming framework that allows users to create and manage graphical components and connections using a visual interface. It is built on top of Java Swing and provides tools for creating, connecting, and interacting with custom GUI components.

## Features

- **Visual Programming Interface**: Drag-and-drop components to create visual workflows.
- **Custom Components**: Extendable `BPComponent` class for creating reusable graphical elements.
- **Node-Based Connections**: Connect components using input and output nodes.
- **Pan**: Navigate large workspaces with pan functionality.
- **Resizable Components**: Resize components dynamically.
- **Interactive Connections**: Highlight and interact with connections between components.
- **Modular Design**: Easily extendable with custom components and logic.

## Project Structure

The project is organized into the following key components:

### **Core Classes**
- **`BPViewport`**: The main canvas where components and connections are displayed. Handles user interactions like dragging, zooming, and connecting nodes.
- **`BPComponent`**: Represents individual graphical components that can be added to the viewport. Supports resizing, moving, and state management.
- **`Connection`**: Represents connections between nodes in different components. Handles drawing and interaction logic.
- **`BPNode`**: Represents input/output nodes for components, allowing connections between them.

### **Example Usage**
- **`Test`**: A sample implementation demonstrating how to use the framework to create and connect components.

## Requirements

- **Java 8 or higher**: The framework requires at least Java 8 to run.
 components.
- **Ant (optional)**: For building the project using the provided `build.xml`.

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/BluePrints.git
    ```

2. Open the project in your favorite Java IDE (e.g., NetBeans, IntelliJ IDEA, Eclipse).

3. Build the project using your IDE's build tools or using `ant` if you are using the provided `build.xml`.

## Usage

### Creating Components
To create a custom component, extend the `BPComponent` class and override its methods as needed:

```java
public class MyComponent extends BPComponent {
     public MyComponent() {
          super();
          // Custom initialization
     }
}
```

### Adding Viewport to BPDesktop

The framework provides a `BPDesktop` class that acts as a container for the `BPViewport`. To add a viewport to the desktop, use the following code:

```java
BPDesktop desktop = new BPDesktop();
BPViewport viewport = new BPViewport();
desktop.addViewport(viewport);
JFrame frame = new JFrame("Test");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setLayout(new BorderLayout());
frame.add(desktop);
```

This will embed the viewport into the desktop, allowing you to manage components and connections within the desktop environment.


### Adding Components to the Viewport
Add components to the `BPViewport` to display them:

```java
BPViewport viewport = new BPViewport();
BPComponent component = new BPComponent();
component.setBounds(100, 100, 200, 100);
viewport.add(component);
```

### Adding Nodes to Components
To add nodes to a `BPComponent`, use the `addNode` method. For example, in `Test.java`:

```java
BPComponent component = new BPComponent();
//BPNode(TypeClass.class, isInput)
BPNode inputNode = new BPNode(Integer.class,true);
BPNode outputNode = new BPNode(String.class,false);

component.addNode(¨id¨,inputNode);
component.addNode(¨id2¨,outputNode);
```

This will add an input node and an output node to the component, which can then be connected to other nodes.

### Connecting Nodes
Use the `connect` method in `BPViewport` to create connections between nodes:

```java
BPNode node1 = component1.getNodes().get(0);
BPNode node2 = component2.getNodes().get(0);

viewport.connect(node1, node2);
```

## License

This project is licensed under the Apache License 2.0. See the LICENSE file for details.

## Author

Salvador Vera Franco

## Contributing

Contributions are welcome! Feel free to fork the repository and submit pull requests.