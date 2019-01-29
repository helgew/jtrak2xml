# ![JTrak2XML Icon](logo/jtrak2xml.iconset/icon_128x128.png?raw=true "JTrak2XML Icon") JTrak2XML 

JTrak2XML converts dive log information managed with [JTrak](https://www.frobese.de/JTrak/en/jtrak.html) to generic XML-based formats.
These can then be imported into other dive log software applications.

## Supported Formats

* [UDDF 3.2.1](https://www.streit.cc/extern/uddf_v321/en/index.html)
* [MacDive XML](http://www.mac-dive.com)

## Getting Started

To convert your dive log into one of the XML formats supported, [download the latest release 
(1.0-beta-3)](https://github.com/helgew/jtrak2xml/releases/download/jtrak2xml-1.0-beta-3/jtrak2xml-1.0-beta-3.dmg), open the disk image file and 
run the application. Depending on your security settings, you may or may not have to [tell OS X 
that indeed you want to run the application](https://www.mcvsd.org/tips/powerteacher/osx_unidentified_developers.html). 

While not tested yet, you should also be able to convert JTrak logs on Linux or Windows.
Simply [download the jar file](https://github.com/helgew/jtrak2xml/releases/download/jtrak2xml-1.0-beta-3/jtrak2xml.1.0-beta-3.jar)
and run the jar from the commandline in a terminal:

```
java -jar jtrak2xml.1.0-beta-3.jar
```

### Prerequisites

You do need to have JTrak installed on your computer. JTrak2XML does not contain any of the 
binary code distributed with JTrak, but it relies on it to do the conversion.

## Limitations

JTrak has a limited set of capabilities that are reflected in the exported data. For example, 
equipment (tanks, suit, computer) is catalogued on a "per-dive" basis. That means that there is 
no way to tell if that 100 cft. steel tank #1 for dive #1 is the same tank as the one with the 
same volume and material used in dive #2. JTrak2XML will treat tanks as identical if they are 
made of the same material, are in the same slot (JTrak allows for up to three tanks), and have 
the same volume. Likewise with suits: same material and weight makes it the same suit. Since 
computers have a serial number associated with them (read from the computer directly), they can 
be managed a bit more intelligently.

Locations in JTrak are described in free text and assigned a country and a time zone. JTrak2XML 
currently ignores the time zone (although times and dates may well have been converted 
correctly) and will not create a separate place and location.

JTrak has no option to ignore any surface swim at the end of a dive. JTrak2XML will provide this
 option in a later release.

JTrak2XML can currently only export all dives. 

## License

This project is licensed under the GPL version 3 License - see the [LICENSE.txt](LICENSE.txt) file 
for details

## Acknowledgments

* [Nick Shore](mailto:support@mac-dive.com), author of [MacDive](http://www.mac-dive.com) 
provided invaluable feeback on the correct structure of the UDDF and MacDive XML formats.
