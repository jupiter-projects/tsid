#!/usr/bin/env bash

native-image -cp "./lib/picocli-4.7.5.jar:./lib/hypersistence-tsid-2.1.1.jar:./target/tsid-0.0.1.jar" --no-server --static -H:Name=tsid com.julianjupiter.tsid.Tsid