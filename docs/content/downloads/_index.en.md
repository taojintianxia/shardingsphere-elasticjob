+++
pre = "<b>6. </b>"
title = "Downloads"
weight = 6
chapter = true

extracss = true

+++

## Latest Releases

ElasticJob is released as source code tarballs with corresponding binary tarballs for convenience. 
The downloads are distributed via mirror sites and should be checked for tampering using GPG or SHA-512.

##### ElasticJob - Version: 3.0.3 ( Release Date: Mar 31, 2023 )

- Source Codes: [ [SRC](https://www.apache.org/dyn/closer.lua/shardingsphere/elasticjob-3.0.3/apache-shardingsphere-elasticjob-3.0.3-src.zip) ] [ [ASC](https://downloads.apache.org/shardingsphere/elasticjob-3.0.3/apache-shardingsphere-elasticjob-3.0.3-src.zip.asc) ] [ [SHA512](https://downloads.apache.org/shardingsphere/elasticjob-3.0.3/apache-shardingsphere-elasticjob-3.0.3-src.zip.sha512) ]
- ElasticJob Binary Distribution: [ [TAR](https://www.apache.org/dyn/closer.lua/shardingsphere/elasticjob-3.0.3/apache-shardingsphere-elasticjob-3.0.3-lite-bin.tar.gz) ] [ [ASC](https://downloads.apache.org/shardingsphere/elasticjob-3.0.3/apache-shardingsphere-elasticjob-3.0.3-lite-bin.tar.gz.asc) ] [ [SHA512](https://downloads.apache.org/shardingsphere/elasticjob-3.0.3/apache-shardingsphere-elasticjob-3.0.3-lite-bin.tar.gz.sha512) ]

##### ElasticJob-UI - Version: 3.0.2 ( Release Date: Oct 31, 2022 )

- Source Codes: [ [SRC](https://www.apache.org/dyn/closer.lua/shardingsphere/elasticjob-ui-3.0.2/apache-shardingsphere-elasticjob-3.0.2-ui-src.zip) ] [ [ASC](https://downloads.apache.org/shardingsphere/elasticjob-ui-3.0.2/apache-shardingsphere-elasticjob-3.0.2-ui-src.zip.asc) ] [ [SHA512](https://downloads.apache.org/shardingsphere/elasticjob-ui-3.0.2/apache-shardingsphere-elasticjob-3.0.2-ui-src.zip.sha512) ]
- ElasticJob-UI Binary Distribution: [ [TAR](https://www.apache.org/dyn/closer.lua/shardingsphere/elasticjob-ui-3.0.2/apache-shardingsphere-elasticjob-3.0.2-lite-ui-bin.tar.gz) ] [ [ASC](https://downloads.apache.org/shardingsphere/elasticjob-ui-3.0.2/apache-shardingsphere-elasticjob-3.0.2-lite-ui-bin.tar.gz.asc) ] [ [SHA512](https://downloads.apache.org/shardingsphere/elasticjob-ui-3.0.2/apache-shardingsphere-elasticjob-3.0.2-lite-ui-bin.tar.gz.sha512) ]

## All Releases

Find all releases in the [Archive repository](https://archive.apache.org/dist/shardingsphere/).

## Verify the Releases

[PGP signatures KEYS](https://downloads.apache.org/shardingsphere/KEYS)

It is essential that you verify the integrity of the downloaded files using the PGP or SHA signatures. 
The PGP signatures can be verified using GPG or PGP. Please download the KEYS as well as the asc signature files for relevant distribution. 
It is recommended to get these files from the main distribution directory and not from the mirrors.

```shell
gpg -i KEYS
```

or

```shell
pgpk -a KEYS
```

or

```shell
pgp -ka KEYS
```

To verify the binaries/sources you can download the relevant asc files for it from main distribution directory and follow the below guide.

```shell
gpg --verify apache-shardingsphere-********.asc apache-shardingsphere-elasticjob-*********
```

or

```shell
pgpv apache-shardingsphere-elasticjob-********.asc
```

or

```shell
pgp apache-shardingsphere-elasticjob-********.asc
```
