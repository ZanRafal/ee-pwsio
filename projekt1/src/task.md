Instrukcje
Tytuł: Wielowątkowość wysokopoziomowa.

Pakiet: java.util.concurrent.

Cel: zapoznanie się z podejściem wysokopoziomowym do tworzenia wątków i zarządzania nimi. W projekcie zakładane jest wykorzystanie implementacji puli wątków (ang. thread pools). Głównym zadaniem w projekcie jest przeprowadzenie analizy porównawczej, której przedmiotem będzie technika klasyczna wykorzystująca interfejs Runnable i klasę Thread oraz metoda wysokopoziomowa z wykorzystaniem interfejsów Executor, ExecutorService i ScheduledExecutorService.

Wymagania:

1.) Wybór zadania programistycznego związanego z wykorzystaniem wielu wątków (np. aplikacja generująca animację, której elementy poruszane są za pomocą różnych wątków).

2.) Implementacja aplikacji z punktu 1 z wykorzystaniem klasycznych technik -- interfejs Runnable i klasa Thread.

3.) Implementacja z wykorzystaniem klas reprezentujących pule wątków (np. ThreadPoolExecutor).

4.) Analiza działania aplikacji z wykorzystaniem profilera (pomiar czasu wykonania, zużycia procesora i zmienności stanów wątków).

5.) Przedstawienie wyników w postaci sprawozdania lub ustnego omówienia w trakcie zajęć, które posłuży sformułowaniu ostatecznych wniosków dotyczących porównania obu podejść poddanych analizie i ocenie w punkcie 4.