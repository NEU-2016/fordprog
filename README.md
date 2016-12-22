# Fordító programok - beadandó

## Nyelv

A beadandó egy okbjektumokat feldolgozó nyelv interpreterjét és compiler-jét foglalja magába.
A nyelv szintaktikailag hasonló alapokon alapszik, mint a Java.

## Szemantika

* `main` metódus egyszerre csak egy szerepelhet egy fájlban, az is csupán a legfelsőbb szinten deklarált osztályok közül egyben, és kötelezően statikus módosítóval ellátott
* Minden osztály csupán egyetlen egyedi konstruktorral rendelkezhet, ám ez bármennyi paraméterrel
* Osztályonként a metódusnevek nem túlterhelhetőek, tehát egy osztályon belül egy metódus csupán egyszer definiálható, paraméterlistától függetlenül
* Osztályonként a változónevek egyediek
* Metódusonként a változónevek egyediek
* Minden osztálynak kötelezően le kell származtatni egy másik osztályt (Java-ban ez a rejtett `extends Object`)

## Sajátosságok

* Egy saját statikus metódussal lehet "kiíratni" a nyelvben, ez a `Println()` metódus, mely üres paraméterlista esetén csupán újsort ír, paraméter esetén a literált
* Elemzés végén a compiler feltűnteti a package nevét, az importok listáját, és a legfelsőbb szinten generált osztályokat.

## Esetleges compiler hibák

* Duplikált importok
* Duplikált osztályok
* Kiterjesztetlen osztályok
* Hiányzok statikus `main` metódus
* Túl sok konstruktor
* Duplikált metódusok
* Duplikált mezők
* Duplikált lokális mezők