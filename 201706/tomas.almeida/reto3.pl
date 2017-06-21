:- use_module(library(clpfd)).

change2([HeadCoin|TailCoin],[HeadQuantity|TailQuantity],Change,Total,[UsedCoins|UsedTail]) :-
    UsedCoins in 0..HeadQuantity,
    NewChange in 0..Change,
    NewChange #= Change - UsedCoins*HeadCoin,
    change(TailCoin,TailQuantity,NewChange,TailUsedCoins,UsedTail),
    Total #= UsedCoins + TailUsedCoins.

change2([HeadCoin],[HeadQuantity],Change,UsedCoins,[UsedCoins]) :-
   UsedCoins in 0..HeadQuantity,
   indomain(UsedCoins),
   Change #= UsedCoins*HeadCoin.

bestSolution2(Coins,Quantity,Change,Solution) :-
    findall([Total,Result],change2(Coins,Quantity,Change,Total,Result),ListOfSolutions).
    smallSum(ListOfSolutions, [_,Solution]).
.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%555555

change([HeadCoin|TailCoin],[HeadQuantity|TailQuantity],Change,Total,[UsedCoins|UsedTail]) :-
    UsedCoins in 0..HeadQuantity,
    NewChange #= Change - UsedCoins*HeadCoin,
    NewChange #>= 0,
    indomain(UsedCoins),
    change(TailCoin,TailQuantity,NewChange,TailUsedCoins,UsedTail),
    Total #= UsedCoins + TailUsedCoins.

change([HeadCoin],[HeadQuantity],Change,UsedCoins,[UsedCoins]) :-
   UsedCoins in 0..HeadQuantity,
   Change #= UsedCoins*HeadCoin.

bestSolution(Coins,Quantity,Change,Solution) :-
    allSolutions(Coins,Quantity,Change,ListOfSolutions),
    smallSum(ListOfSolutions, [_,Solution]).

allSolutions(Coins,Quantity,Change,ListOfSolutions) :-
     findall([Total,Result],change(Coins,Quantity,Change,Total,Result),ListOfSolutions).

 smallSum([Head],Head).

 smallSum([Head|Tail],Result) :-
     smallSum(Tail,ResultTail),
     smaller(Head,ResultTail, Result).

smaller([SumA, ListA], [SumB,_], [SumA,ListA]) :-
    SumA #=< SumB.
smaller([SumA, _], [SumB,ListB], [SumB,ListB]) :-
    SumB #< SumA.
