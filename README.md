# Binary Partition Tree construction from multiple features for image segmentation
Java implementation of [Binary Partition Tree construction from multiple features for image segmentation](https://www.sciencedirect.com/science/article/abs/pii/S0031320318302358) published in the Pattern Recognition journal in 2018.

## Authors
Jimmy Francky Randrianasoa, Camille Kurtz, Éric Desjardin, Nicolas Passat

## Abstract
In the context of image analysis, the Binary Partition Tree (BPT) is a classical data structure for the hierarchical modelling of images at different scales. BPTs belong both to the families of graph-based models and morphological hierarchies. They constitute an efficient way to define sets of nested partitions of image support, that further provide knowledge-guided reduced research spaces for optimization-based segmentation procedures. Basically, a BPT is built in a mono-feature way, i.e. for one given image, and one given metric, by merging pairs of connected image regions that are similar in the induced feature space. Our goal is to design a new family of BPTs, dealing with the need to directly manage multiple features within its building process. Then, we propose a generalization of the BPT construction framework, allowing one to embed multiple features. The cornerstone of our approach relies on a collaborative strategy used to establish a consensus between different metrics, thus enabling to obtain a unified hierarchical segmentation space. In particular, this provides alternatives to the complex issue of metric construction from several —possibly non-comparable— features. To reach that goal, we first revisit the BPT construction algorithm to describe it in a graph-based formalism. Then, we present the structural and algorithmic evolutions and impacts when embedding multiple features in BPT construction. Final experiments illustrate how this multi-feature framework can be used to build BPTs from multiple metrics computed through the (potentially multiple) image content(s).

## Illustrations

![](fig1.png)

---

![](fig3.png)

## Usage

<b>BPT:</b> Binary Partition Tree </br>
<b>MBPT:</b> Multi-feature Binary Partition Tree

<b>Language:</b> Java </br>
<b>Purpose:</b> Creating and managing BPT and MBPT </br>

<b>Core classes:</b> </br>
&bull; [BPT](src/standard/sequential/BPT.java)</br>
&bull; [MBPT](src/multi/sequential/MBPT.java) </br>

<b>Project dependency:</b> 
[Image](https://github.com/yonmi/Image) </br>

<b>Getting started:</b> you can start with some [examples](src/examples)

## How to cite

    Jimmy Francky Randrianasoa, Camille Kurtz, Éric Desjardin, Nicolas Passat,
    Binary Partition Tree construction from multiple features for image segmentation,
    Pattern Recognition,
    Volume 84,
    2018,
    Pages 237-250,
    ISSN 0031-3203,
    https://doi.org/10.1016/j.patcog.2018.07.003.
    (https://www.sciencedirect.com/science/article/pii/S0031320318302358)

**bibtex**

    @article{RANDRIANASOA2018237,
    title = {Binary Partition Tree construction from multiple features for image segmentation},
    journal = {Pattern Recognition},
    volume = {84},
    pages = {237-250},
    year = {2018},
    issn = {0031-3203},
    doi = {https://doi.org/10.1016/j.patcog.2018.07.003},
    url = {https://www.sciencedirect.com/science/article/pii/S0031320318302358},
    author = {Jimmy Francky Randrianasoa and Camille Kurtz and Éric Desjardin and Nicolas Passat},
    }

## Third-party libraries
This project bundles some third-party libraries:
- [jai_codec](https://www.oracle.com/java/technologies/advanced-imaging-api.html), [jai_core](https://www.oracle.com/java/technologies/advanced-imaging-api.html), [jai_imageio](https://www.oracle.com/java/technologies/advanced-imaging-api.html) are parts of the <b>Java Advanced Imaging API</b> which is a set of image encoder/decoder (codec) classes - [Java Research License (JRL)](https://github.com/mauricio/jai-core/blob/master/LICENSE-JRL.txt)
- [sis-jhdf5](http://svnsis.ethz.ch/repos/cisd/ivy-repository/trunk/sis/sis-jhdf5/14.12.1/) helps to manage <b>HDF5</b> file formats - [Apache License 2.0](
http://www.apache.org/licenses/LICENSE-2.0)
