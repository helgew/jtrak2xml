# JTrak2XML

JTrak2XML converts dive log information managed with [JTrak](https://www.frobese.de/JTrak/en/jtrak.html) to generic XML-based formats. These can then be imported into other dive
 log software applications.

## Supported Formats

* [UDDF 3.2.1](https://www.streit.cc/extern/uddf_v321/en/index.html)
* [MacDive XML](http://www.mac-dive.com)

## Getting Started

To convert your dive log into one of the XML formats supported, [download the latest release 
(1.0-alpha-1)](https://github.com/helgew/jtrak2xml/releases/download/jtrak2xml-1.0-alpha-1/jtrak2xml.1.0-alpha-1.jar) and run the 
jar 
from 
a terminal. For example, in OS X, you 
would start the Terminal.app (look for it under `/Applications/Utilities`) and run the follwing 
command (assuming you downloaded the distribution to your `Downloads` folder):

```
java -jar ~/Downloads/jtrak2xml.1.0-alpha-1.jar
```

### Prerequisites

You do need to have JTrak installed on your computer. JTrak2XML does not contain any of the 
binary code distributed with JTrak, but it relies on it to do the conversion.

## Authors

* **Helge Weissig** - *Initial work* - [helgew](https://github.com/helgew)

## License

This project is licensed under the GPL version 3 License - see the [LICENSE.txt](../../../LICENSE.txt) file 
for details

## Acknowledgments

* [Nick Shore](mailto:support@mac-dive.com), author of [MacDive](http://www.mac-dive.com) 
provided invaluable feeback on the correct structure of the UDDF and MacDive XML formats.
