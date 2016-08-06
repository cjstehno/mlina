/*
## Command to generate the layout: "neato -Tpng thisfile > thisfile.png"

digraph DecisionDiagram {
    node [shape=box];  no_surfacing; flippers;
    node [shape=circle,fixedsize=true,width=0.9];  no; maybe; yes;

    no_surfacing->no [label = "0"];
    no_surfacing->flippers [label = "1"];
    no_surfacing->maybe [label = "3"];
    flippers->no [label = "0"];
    flippers->yes [label = "1"];

    overlap=false
    fontsize=12;
}

This is just a placeholder for generating the tree plot using graphviz - I will save that for later (unless it is used for something)
 */

println 'Hello GraphViz!'