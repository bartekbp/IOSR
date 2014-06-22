1. Instalacja środowiska
------------------------
Pierwszy krok; pobrać:

    http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html

i umieścić w vagrant/files/.

Nie jest to piękne, ale działa, i vagrant nie musi ściągać javy dla każdej jednej VMki podczas stawiania środowiska.

UWAGA: Potrzebne jest dużo wolnego miejsca na dysku (30GB). Domyślnie wszystko wyląduje w "~/VirtualBox VMs". Aby zmienić tę lokalizację, trzeba odpalić virtualbox'a, wejść w preferencje i zmienić domyślny katalog.

Pobrać i zainstalować najnowszą wersje Vagrant'a i VirtualBoxa 4.2 (koniecznie ta wersja, ze względu na wersję guest additions w boxach vagrantowych):
    
    https://www.vagrantup.com/downloads.html
    https://www.virtualbox.org/wiki/Download_Old_Builds_4_2


2. Stawianie środowiska (maszyn wirtualnych)
--------------------------------------------
UWAGA: Obecna konfugaracja wymaga 11GB RAMu. Jeśli to za dużo, należy edytować Vagrantfile i zmniejszyć ilość pamięci przydzielanej poszczególnym VMkom.

Wejść do katalogu vagrant/ i odpalić:

    vagrant up

Jeśli nie potrafi pobrać sobie box'a ("An error occurred while downloading the remote file..."), wtedy:

    vagrant box add precise64 http://files.vagrantup.com/precise64.box

I spróbować ponownie. Jeśli "vagrant up" zwróci nam prompta bez sypania błędami, to znaczy, że środowisko wstało.

Za pierwszym uruchomieniem środowiska, Vagrant wykona tzw. "provisioning" - skrypty, które ustawią /etc/hosts, zainstalują javę etc. Przy wznowieniach skrypty te są pomijane. Jeśli z jakiegoś powodu chcemy ponownie odpalić te skrypty, można wykonać (gdy środowiko jest postawione):

    vagrant provision

Dodać wpisy z pliku vagrant/files/hosts do lokalnego /etc/hosts, aby móc posługiwać się hostname'ami np. w przeglądarce.


3. Wstrzymywanie, kasowanie środowiska
--------------------------------------
Aby wstrzymać wszystkie VMki, a potem wznowić pracę w tym samym miejscu, używamy:

    vagrant halt
    vagrant up

Natomiast aby całkowicie usunąć środowisko:

    vagrant destroy


4. Używanie środowiska
----------------------
Na poszczególne VMki logujemy się przy użyciu komendy vagrant ssh podając nazwę VMki (zgodną z definicjami w Vagrantfile). Przykładowo:

    vagrant ssh kafka-node1

W celu skopiowania jakiegoś pliku na VMkę z własnej maszyny, najłatwiej korzystać z następującego mechanizmu - wszystko wrzucone do folderu zawierającego Vagrantfile (u nas w repo katalog vagrant/) jest podmontowane pod ścieżką /vagrant na vmkach.


5. Stawianie clustra cloudery
------------------------------------------

Wejść przez przeglądarkę na adres:
    
    http://cloudera-master:7180/
    login: admin
    hasło: admin

Skonfigurować cluster cloudery za pomocą wizarda, który nam się wyświetlił.

    1. Wybrać wersję darmową
    2. Continue
    3. Wpisać "cloudera-master, cloudera-slave1" w okno wyszukiwania i dać search, continue jak wyszuka node'y
    4. Instalacja klastra
        Krok 1: Continue
        Krok 2: "Another User" - "vagrant", "All hosts accept same password" - "vagrant" i continue
        Krok 3: Poczekać na zakończenie instalacji, continue
        Krok 4: Poczekać na zakończenie instalowania oprogramowania na hostach, continue
        Krok 5: Poczekać na sprawdzenie, czy wszystko się poprawnie zainstalowało
    5. Instalacja serwisów - wybieramy All services
    6. Wybór baz danych - test connection, potem continue
    7. Configuration changes - continue
    8. Poczekać na instalację wszystkich pakietów, przeklikać do końca instalacji


6. Budowanie projektu i deploy na wirtualne maszyny
---------------------------------------------------
W głównym folderze projektu jest Makefile. Odpalamy

    make

I paczka z naszym kaflogiem będzie dostępna na każdej maszynie wirtualnej pod 

    /vagrant/files/kaflog-0.1/


7. Odpalanie mastera
--------------------
Warunkiem początkowym jest postawienie cloudery na cloudera-master (no i slavach w miarę potrzeb) - patrz punkt 5.
Na cloudera-master odpalamy:

    /vagrant/files/kaflog-0.1/bin/kaflog_broker.sh 
    /vagrant/files/kaflog-0.1/bin/kaflog_master.sh 


8. Używanie KaflogProducera
---------------------------
Warunkiem początkowym jest wykonianie punktu 7. 
Na każdym kafka-node, który chcemy podłączyć, odpalamy:

    /vagrant/files/kaflog-0.1/bin/kaflog_producer.sh 

W tym momencie działa nasz producent i będzie publikował logi z sysloga. Zalogować coś do sysloga można na trzy sposoby:

    logger "tresc loga"
    /vagrant/files/kaflog-0.1/bin/log_generator.sh <ilosc_logow>
    /vagrant/files/kaflog-0.1/bin/log_generator_per_min.sh <ilosc_logow_na_minute>

Wejść na:

    http://cloudera-master:8080

Zrobić popcorn i oglądać.


9. Używanie HadoopConsumera
---------------------------
Warunkiem początkowym jest wykonianie punktu 7.  
Na cloudera-master odpalamy:

    /vagrant/files/kaflog-0.1/bin/kaflog_hadoop_consumer.sh

Zrobić makaron i nie zapomnieć dodać soli.


10. Używanie Storm Consumera
---------------------------
Warunkiem początkowym jest wykonianie punktu 7. 
Domyślnie storm działa na cloudera-master i cloudera-slave1.

Trzeba uruchomić cluster storma:

    # @cloudera-master: tu odpalamy nimbusa - taki storm-master
    ~/storm-0.8.1/bin/storm nimbus &
    # Warto uruchomić także ui aby można było śledzić status naszej topologii.
    # UI jest dostępny pod adresem cloudera-master:8088
    ~/storm-0.8.1/bin/storm ui &

    # @cloudera-slave1: tu odpalamy supervisora
    ~/storm-0.8.1/bin/storm supervisor    
 

Uruchmoić topologie:

    # @cloudera-master
    /vagrant/files/kaflog-0.1/bin/kaflog_storm_consumer.sh

Zabijanie topologi

    # nasza topologia nazywa się 'storm'
    ~/storm-0.8.1/bin/storm kill storm


11. Import danych z Hdfs do Hive
--------------------------------
Trzeba mieć działającą odpowiednią rolę - hiveserver2. Dodaje się ją przez services/hive/add w GUI Cloudery.

Dodatkowo należy dodać sobie użytkownika vagrant do cloudery i nadać uprawnienia 
    
    sudo -u hdfs hadoop fs -chmod 777 /user/hive.

Polecam też dodanie zookeepera na cloudera-slave1.
Dodać hbaserestserver i hbasethriftserver w GUI Cloudery.


12. Podział na podmoduły
--------------------------------
Główny moduł to kaflog. Jest to parent pom dla prawie wszystkich projektów poza mastermonitoring. Dla mastermonitoring parentem jest spring-boot-starter-parent.

Mastermonitoring to webappka, ale można ją odpalać przez java -jar.
Alternatywny sposób to odpalanie przez mvn spring-boot:run - to się przydaje podczas pracy.


13. Uwagi do dokumentacji kafki (uzywamy wersji 0.8.1, a w ebooku jest 0.72/0.8)
-------------------------------------------------------------------------------
Proponuję tutaj dopisywać rzeczy, które należy wiedzieć jeśli chodzi o kafkę 0.8.1 (w szczególności to, co się zmieniło względem starych wersji).

Zmieniono sposób tworzenia topica

    stary -> bin/kafka-create-topic.sh --zookeeper localhost:2181 --replica 1 --partition 1 --topic kafkatopic
    nowy ->  bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partition 1 --topic kafkatopic