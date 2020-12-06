# Advent Of Code 2020
Solutions in [Kotlin](https://www.kotlinlang.org/), the most fun language on the planet!

My solutions to the ingenious [Advent Of Code 2020](https://adventofcode.com/)
by Eric Wastl.

This is my fourth year of Advent of Code coding in a row - my body gets trained again
to get up at 5:45 in the morning for almost a month... the addiction is real!

If you are into programming, logic, maybe also a little into competition, this one is for you as well!

### Overview of the puzzles
|Day |Title                             |Remarks
|---:|----------------------------------|----|
|  1 |Report Repair                     |For a day 1 puzzle, quite challenging! More than just a sum of ints...|
|  2 |Password Philosophy               |Category "parse, validate & count" - easily done.|
|  3 |Toboggan Trajectory               |The first map grid puzzle of 2020, but IMHO not a very beautiful one.|
|  4 |Passport Processing               |A puzzle full of text processing and arbitrary checks on data...|
|  5 |Binary Boarding                   |Turns out to be *VERY* simple. Map a string to binary 0/1 and answer some questions about it|
|  6 |Custom Customs                    |Whoop, whoop, simple one again. What is Eric doing?| 

## My logbook of 2020

### Day 1: Report Repair
Anxiously awaiting the first puzzle of 2020, usually a matter of very few minutes for me, it was kind of
tough to get up that early. Knowing I could theoretically go back to bed quite soon, I managed to do it...
Well, but then...
[2020 stays exactly like we are all used to by now](https://www.reddit.com/r/adventofcode/comments/k4ejjz/2020_day_1_unlock_crash_postmortem/).
Hitting server errors when loading the puzzle page, timeouts and such, I got really nervous quite quickly.

When the puzzle finally loaded, it was a real surprise, and I was surprised it was not as easy as the years before.
No: "sum up all numbers" here. More like: "find the combination of two that sum up to 2020".
Well, me being a functionally oriented programmer, I knew: go through all combinations of 2 until there's a sum of 2020.
Easy. So, my [favorite Math library for Kotlin](https://github.com/MarcinMoskala/KotlinDiscreteMathToolkit) provides a 
combination function - let's do this. Turns out: internally the implementation calculates *all* combinations of *all*
sizes, called the powerset, and then filters by the desired size. Gee... 200 elements have 2^200 sets in its powerset!
No way this can be calculated quickly - and so I waited. Seconds. A minute. I knew something went very wrong for day 1!

My then quickly hacked imperative solution lacked a proper block structure for the break statement out of the inner loop
and - bam - wrong answer on day 1, part 1! :-)

What a hefty start.

With my own extension function for combinations of elements the whole code looks like this:
`report.combinations(2).first { it.sum() == 2020 }.product()` for part 1 and 
`report.combinations(3).first { it.sum() == 2020 }.product()` for part 2. Easy peasy.

Lesson learned today: 2020 is f***ed up! Thankfully, day 1 did not give any points.

### Day 2: Password Philosophy
Warm up puzzle from the category "parse, validate & count".
Each individual input line is a password policy with a concrete password that either meets or fails the requirement.

As the hunt for the first points were on again, it was a real race...

### Day 3: Toboggan Trajectory
A was happy to see that this one was the first map grid puzzle and already felt the urge to visualize this world of
trees and free spaces. But it turned out, this one had no real qualities, at least to me.

The only challenging part was the infinite repetition of the grid to the right.
In Kotlin, I quickly came up with an `EndlessList` implementation, using interface delegation by a backing list and
overwriting only the size (to return MAX_INT, not infinity...) and the indexing operation.

```
val el = EndlessList(listOf("a", "b", "c"))
println(el.size)  // prints Int.MAX_VALUE
println(el)       // would print ["a", "b", "c", "a", "b", "c", "a", ..........
```

### Day 4: Passport Processing
This puzzle deserves a place in the top 10 worst puzzles list...

Process a bunch of passports' data fields and read *very* carefully. Lots of typos can happen, most of the checks
and field names required need to be manually typed as they cannot easily be copy&pasted from the puzzle text.

A real starting challenge was to correctly preprocess the passport data as the delimiter between individual 
passports were simply blank lines and line breaks themselves delimit nothing.

The aftermath of this puzzle led me to transform the validation mechanism into a validator map with keys (the field
names) and a corresponding lambda expression for the validation work. This at least looks quite nice.

### Day 5: Binary Boarding
Hell, was I dumb in the morning. It looked deceivingly simple at first, then got awkward doing it manually and an hour
later it all crumbled to *very* simple again.

First mistake: I probably mixed up the Fs and Bs and did it the wrong way. That's why I then turned to do the binary
translation manually with a fold command, folding a range of 0..127 down to the single digit.
For part 2, I again overdid it by a lot! I understood the question differently: look for the first full-seated row
(seats 0 through 7), followed by a row with one free seat, followed by another full-seated row. This worked. But hey:
Eric clearly stated: "ID-1 and ID+1 is taken" No word about the complete row in front and behind.

An hour later, everything got clear. The whole row/column definition was a scam. All this was: Fs are 0, Ls are 0, 
the rest is a 1. Take that string, interpret it as a binary number. Do that for all the boarding passes.

Then answer the question: which is the highest id? And in the range of the lowest to the highest, which single one
is missing - that's our seat. Done.

### Day 6: Custom Customs
Alright, another simple challenge. Mixing day 4's splitting by empty lines, all this puzzle wanted to teach us is the 
difference between a union and an intersection of characters?!? This is underwhelming to say the least.

In other years, the level had been much higher on a weekend day 6...
It's bed time - again! Good night.