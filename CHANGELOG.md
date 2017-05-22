# Release Notes

## 0.13.0 (2017-01-20)

- Upgrade to Java 8
- Improve support for requested initial sequence number in MoldUDP64 client
- Add downstream sequence number and message count parameters to MoldUDP64
  client status listener

## 0.12.0 (2016-12-20)

- Improve project structure
- Improve documentation
- Add requested initial sequence number support to MoldUDP64 client

## 0.11.0 (2016-11-27)

- Make SoupBinTCP server address configurable in SoupBinTCP gateway
- Update example configuration file for SoupBinTCP gateway
- Update example configuration file for BinaryFILE recorder
- Remove GZIP support from BinaryFILE writer
- Improve BinaryFILE write performance
- Add SoupBinTCP support to BinaryFILE recorder
- Fix sequence number in SoupBinTCP gateway
- Fix sequence number in SoupBinTCP utilities

## 0.10.0 (2016-07-22)

- Refactor SoupBinTCP implementation
- Remove dependency on NIO Extras 0.1.0
- Add dependency to Foundation 0.2.0
- Reduce memory allocation in SoupBinTCP implementation
- Add BinaryFILE write support
- Add BinaryFILE recorder
- Fix BinaryFILE reader interface
- Add utility methods for BinaryFILE file format
- Add utility methods for MoldUDP64 protocol
- Add utility methods for SoupBinTCP protocol


0.9.0 (2016-03-28)
------------------

- Move to `com.paritytrading` namespace


0.8.1 (2016-03-28)
------------------

- Improve API documentation


0.8.0 (2016-03-28)
------------------

- Add session parameter to MoldUDP64 client status listener
- Add session parameter to SoupBinTCP session status listeners


0.7.0 (2015-12-26)
------------------

- Improve SoupBinTCP API
- Improve SoupBinTCP performance
- Add SoupBinTCP gateway

## 0.6.0 (2015-10-06)

- Introduce new MoldUDP64 client

## 0.5.0 (2015-09-27)

- Improve MoldUDP64 gap fill performance
- Improve MoldUDP64 default message store performance
- Improve BinaryFILE performance
- Add BinaryFILE performance test
- Improve API documentation
- Add SoupBinTCP performance test

## 0.4.0 (2015-04-13)

- Add client state to MoldUDP64
- Add lower bound for built-in receive buffer size in SoupBinTCP
- Fix requested message count in MoldUDP64
- Fix end of session handling in MoldUDP64
- Add BinaryFILE support
- Add single-channel and multi-channel client for MoldUDP64

## 0.3.0 (2014-10-29)

- Fix message count handling in MoldUDP64
- Improve built-in payload transmit buffer size in SoupBinTCP
- Add session keep-alive to MoldUDP64
- Make built-in receive buffer size configurable in SoupBinTCP

## 0.2.0 (2014-08-03)

- Improve numeric handling in SoupBinTCP
- Add heartbeat timeout event to SoupBinTCP
- Add MoldUDP64 support

## 0.1.0 (2014-05-16)

- Initial release
