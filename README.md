![Cover](https://raw.github.com/dzavodnikov/JcvLib/master/src/test/resources/Cover.jpg)

![Travis JcvLib master status](https://travis-ci.org/dzavodnikov/JcvLib.svg?branch=master)


Overview
========
Java Computer Vision Library (`JcvLib`) is a Computer Vision and Image Processing Library for Java.


Requirements
============
 * Java 1.8 or newer.
 * If you want using video -- *OS*: Linux or Windows, *Arch*: i386 or x86/x64.


Documentation
=============
 * [JavaDocs](https://dzavodnikov.github.io/JcvLib/)
 * [Examples](https://github.com/dzavodnikov/JcvLib/tree/examples/)


Use
===
Gradle
------
    ...
	allprojects {
	    ...
		repositories {
			...
			maven {
                name    'Actual Xuggler repo'
                url     'https://raw.github.com/olivierayache/xuggle-xuggler/repos/'
            }
            maven {
                name    'JitPack'
                url     'https://jitpack.io/' 
            }
		}
		...
	}
	...
    dependencies {
        ...
        compile 'com.github.dzavodnikov:JcvLib:4.+'
        ...
    }
    ...

Maven
-----
    <project>
        ...
	    <repositories>
	        ...
	        <repository>
		        <id>Actual Xuggler repo</id>
		        <url>https://raw.github.com/olivierayache/xuggle-xuggler/repos/</url>
		    </repository>
		    <repository>
		        <id>JitPack</id>
		        <url>https://jitpack.io/</url>
		    </repository>
	    </repositories>
        ...
        <dependencies>
            ...
	        <dependency>
	            <groupId>com.github.dzavodnikov</groupId>
	            <artifactId>JcvLib</artifactId>
	            <version>[4.0.0, 5.0.0)</version>
	        </dependency>
	        ...
	    <dependencies>
	    ...
	</project>

