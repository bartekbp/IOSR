1. Instalacja środowiska
------------------------
Pierwszy krok; pobrać:

    http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html

i umieścić w vagrant/files/.

UWAGA: Potrzebne jest dużo wolnego miejsca na dysku (30GB). Domyślnie wszystko wyląduje w "~/VirtualBox VMs". Aby zmienić tę lokalizację, trzeba odpalić virtualbox'a, wejść w preferencje i zmienić domyślny katalog.

Pobrać i zainstalować najnowszą wersje Vagrant'a i VirtualBoxa 4.2 (koniecznie ta wersja, ze względu na wersję guest additions w boxach vagrantowych):
    
    https://www.vagrantup.com/downloads.html
    https://www.virtualbox.org/wiki/Download_Old_Builds_4_2


2. Stawianie środowiska (maszyn wirtualnych)
--------------------------------------------
UWAGA: Obecna konfugaracja wymaga 2GB RAMu. Jeśli to za dużo, należy edytować Vagrantfile i zmniejszyć ilość pamięci przydzielanej poszczególnym VMkom.

Wejść do katalogu vagrant/ i odpalić:

    vagrant up

Jeśli nie potrafi pobrać sobie box'a ("An error occurred while downloading the remote file..."), wtedy:

    vagrant box add precise64 http://files.vagrantup.com/precise64.box

I spróbować ponownie. Jeśli "vagrant up" zwróci nam prompta bez sypania błędami, to znaczy, że środowisko wstało.

Za pierwszym uruchomieniem środowiska, Vagrant wykona tzw. "provisioning" - skrypty, które ustawią /etc/hosts, zainstalują javę etc. Przy wznowieniach skrypty te są pomijane. Jeśli z jakiegoś powodu chcemy ponownie odpalić te skrypty, można wykonać (gdy środowiko jest postawione):

    vagrant provision

Dodać wpisy z pliku vagrant/files/hosts do lokalnego /etc/hosts, aby móc posługiwać się hostname'ami np. w przeglądarce.


3. Stawianie clustra cloudery
------------------------------------------
TODO: Docelowo będę chciał napisać skrypt, który postawi klaster automatycznie, na razie jednak jest to mniej ważne. Dlatego też trzeba sobie wyklikać klaster przez GUI.

TODO: Na razie nie udało mi się przejść całego procesu instalacji. Póki co można to olać i pobawić się kafką.

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
    8. Tutaj mi się zawsze wywala na instalowaniu Oozie. Trzeba sprawdzić, czy jest w ogóle nam potrzebne


4. Wstrzymywanie, kasowanie środowiska
--------------------------------------
Aby wstrzymać wszystkie VMki, a potem wznowić pracę w tym samym miejscu, używamy:

    vagrant halt
    vagrant up

Natomiast aby całkowicie usunąć środowisko:

    vagrant destroy


5. Używanie środowiska
----------------------
Na poszczególne VMki logujemy się przy użyciu komendy vagrant ssh podając nazwę VMki (zgodną z definicjami w Vagrantfile). Przykładowo:

    vagrant ssh kafka-node1

W celu skopiowania jakiegoś pliku na VMkę z własnej maszyny, najłatwiej korzystać z następującego mechanizmu - wszystko wrzucone do folderu zawierającego Vagrantfile (u nas w repo katalog vagrant/) jest podmontowane pod ścieżką /vagrant na vmkach.

Używanie kafki - na maszyny "kafka-nodexxx" trafiają binarki Kafki, do folderu ~/kafka. Podobnie na maszynę cloudera-master, która docelowo będzie konsumentem Kafkowych wiadomości. Aby postawić usługi Kafki, najlepiej kierować się ebookiem Apache Kafka.


6. Uwagi do dokumentacji kafki (uzywamy wersji 0.8.1, a w ebooku jest 0.72/0.8)
-------------------------------------------------------------------------------
Proponuję tutaj dopisywać rzeczy, które należy wiedzieć jeśli chodzi o kafkę 0.8.1 (w szczególności to, co się zmieniło względem starych wersji).

Zmieniono sposób tworzenia topica

    stary -> bin/kafka-create-topic.sh --zookeeper localhost:2181 --replica 1 --partition 1 --topic kafkatopic
    nowy ->  bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partition 1 --topic kafkatopic


7. Budowanie projektu i deploy na wirtualne maszyny
---------------------------------------------------
W głównym folderze projektu jest Makefile. Odpalamy

    make

I paczka z naszym kaflogiem będzie dostępna na każdej maszynie wirtualnej pod 

    /vagrant/files/kaflog-0.1/


8. Używanie KaflogProducera
---------------------------
Przykład powinien działać w zasetupowanym clouderowym klastrze i być odpalany na kafka-node1.
Odpalamy:

    /vagrant/files/kaflog-0.1/bin/kaflog_producer_all.sh 


W ty momencie działa nasz producent i będzie publikował logi z sysloga. Zalogować coś do sysloga można na dwa sposoby:

    logger "tresc loga"
    /vagrant/files/kaflog-0.1/bin/log_generator.sh <ilosc_logow_na_minute>

Zrobić popcorn i oglądać.

9. Używanie HadoopConsumera
---------------------------
Przykład powienien działać w zasetupowanym clouderowym klastrze i być odpalany na cloudera-master. 
Odpalamy:

    /vagrant/files/kaflog-0.1/bin/kaflog_hadoop_consumer.sh

Zrobić makaron i nie zapomnieć dodać soli.


10. Używanie Storm Consumera
---------------------------
Trzeba doinstalować kilka rzeczy, zatem jednorazowo należy ponownie wykonać provisioning. Cluster będzie postawiony
na nodach cloudera-master i cloudera-slave1.

Nie wiem czemu strasznie muli apt-get. Trzeba poczekać (u mnie ~ 20 minut)

    vagrant provision cloudera-master cloudera-slave1

Następnie trzeba uruchomić cluster storma:

    # @cloudera-master: tu odpalamy nimbusa - taki storm-master
    ~/strom-0.8.1/bin/strom nimbus

    # @cloudera-slave1: tu odpalamy supervisora
    ~/storm-0.8.1/bin/storm supervisior

Wreszcie można odpalić storma

    # @cloudera-master
    /vagrant/files/kaflog-0.1/bin/kaflog_storm_consumer.sh