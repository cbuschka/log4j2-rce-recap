# Little recap of the log4j2 remote code execution (CVE-2021-44228)

## Requirements

* maven >= 3
* java >= 8
* no running ldap on 127.0.0.1:1389

## Usage

Build with maven.

```
mvn test
```

The surefire plugin will be executed two times:

* [ExploitabilityTest](./src/test/java/ExploitabilityTest.java) proofs the expolitability of the bug.

* [MitigationTest](./src/test/java/MitigationTest.java) will be executed with the vm property
```-Dlog4j2.formatMsgNoLookups=true``` and proof the effectiveness of the mitigation.

## In Short

### log4j vs log4j2

log4j2 is __not__ log4j 1.x; both are logging frameworks, but they are API incompatible. log4j 1.x is not affected by this
bug, but may have different security flaws.

### The exploit

* log4j2 evaluates a lookup pattern ( ```${...}``` ) both on formatting string and arguments. so when input from outside is
  logged such patterns can be added and log4j2 will evaluate them.
* the lookup handle for jndi is enable by default, and so it can be triggered by such a lookup pattern, descript above,
  e.g.
  ```${jndi:127.0.0.1:1389/a}```
* because of bugs in the jndi code java is prone to remote code executions
* because of those three parts coming together, remote code executions now can be triggered from outside

## References

* [the cve](https://nvd.nist.gov/vuln/detail/CVE-2021-44228)
* [log4j12 ticket adding log4j2.formatMsgNoLookups](https://issues.apache.org/jira/browse/LOG4J2-2109)
* [mitigation via agent](https://github.com/corretto/hotpatch-for-apache-log4j2)
* [analysis post on stack exchange](https://security.stackexchange.com/a/257933)
* [presentation about flaws in jndi](https://www.blackhat.com/docs/us-16/materials/us-16-Munoz-A-Journey-From-JNDI-LDAP-Manipulation-To-RCE.pdf)
* [mitigation for spring](https://spring.io/blog/2021/12/10/log4j2-vulnerability-and-spring-boot)
* [article on heise in german](https://www.heise.de/news/Kritische-Zero-Day-Luecke-in-log4j-gefaehrdet-zahlreiche-Server-und-Apps-6291653.html)
* [another poc](https://github.com/tangxiaofeng7/CVE-2021-44228-Apache-Log4j-Rce)
* [list of bulletins](https://gist.github.com/SwitHak/b66db3a06c2955a9cb71a8718970c592)
