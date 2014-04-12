1. Instalacja środowiska
------------------------
UWAGA: Potrzebne jest dużo wolnego miejsca na dysku (30GB). Domyślnie wszystko wyląduje w "~/VirtualBox VMs". Aby zmienić tę lokalizację, trzeba odpalić virtualbox'a, wejść w preferencje i zmienić domyślny katalog.

Pobrać i zainstalować najnowszą wersje Vagrant'a i VirtualBoxa 4.2 (koniecznie ta wersja, ze względu na wersję guest additions w boxach vagrantowych):
    
    https://www.vagrantup.com/downloads.html
    https://www.virtualbox.org/wiki/Download_Old_Builds_4_2


2. Stawianie środowiska (maszyn wirtualnych)
--------------------------------------------
UWAGA: Obecna konfugaracja wymaga 2GB RAMu. Jeśli to za dużo, należy edytować Vagrantfile i zmniejszyć ilość pamięci przydzielanej poszczególnym VMkom.

Wejść do katalogu z Vagrantfile i odpalić:

    vagrant up

Jeśli nie potrafi pobrać sobie box'a ("An error occurred while downloading the remote file..."), wtedy:

    vagrant box add precise64 http://files.vagrantup.com/precise64.box

I spróbować ponownie. Jeśli "vagrant up" zwróci nam prompta bez sypania błędami, to znaczy, że środowisko wstało.

Za pierwszym uruchomieniem środowiska, Vagrant wykona tzw. "provision scripts", które ustawią /etc/hosts, zainstalują javę etc. Przy wznowieniach skrypty te są pomijane. Jeśli z jakiegoś powodu chcemy ponownie odpalić te skrypty, można wykonać (gdy środowiko jest postawione):

    vagrant provision


3. Przygotowanie do pracy ze środowiskiem
------------------------------------------
Dodać wpisy z pliku vagrant/files/hosts do lokalnego /etc/hosts, aby móc posługiwać się hostname'ami np. w przeglądarce.

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
Aby wstrzymać wszystkie VMki i potem wznowić pracę w tym samym miejscu, używamy:

    vagrant halt
    vagrant up

Natomiast aby całkowicie usunąć środowisko:

    vagrant destroy


5. Używanie środowiska
----------------------
Na poszczególne VMki logujemy się przy użyciu komendy vagrant ssh podając nazwę VMki (zgodną z definicjami w Vagrantfile). Przykładowo:

    vagrant ssh kafka-node1

W celu skopiowania jakiegoś pliku na VMkę z własnej maszyny, najłatwiej korzystać z następującego mechanizmu. Wszystko wrzucone do folderu zawierającego Vagrantfile (u nas w repo katalog vagrant/) jest podmontowane pod ścieżką /vagrant na vmkach.

Używanie kafki - na maszyny korzystające ze skryptu inicjującego "kafka-node.sh" trafiają binarki Kafki, do folderu ~/kafka. Podobnie na maszynę cloudera-master, która docelowo będzie konsumentem Kafkowych wiadomości. Aby postawić usługi Kafki, najlepiej kierować się ebookiem Apache Kafka (jest w repo pod ebooks/).


6. Uwagi do dokumentacji kafki (uzywamy wersji 0.8.1, a w ebooku jest 0.72/0.8)
-------------------------------------------------------------------------------
Proponuję tutaj dopisywać rzeczy, które należy wiedzieć jeśli chodzi o kafkę 0.8.1 (w szczególności to, co się zmieniło względem starych wersji).

Zmieniono sposób tworzenia topica

    stary -> bin/kafka-create-topic.sh --zookeeper localhost:2181 --replica 1 --partition 1 --topic kafkatopic
    nowy ->  bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partition 1 --topic kafkatopic