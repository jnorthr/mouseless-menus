println "Hello, World!"
x = 1
println x

x = new java.util.Date()
println x

x = -3.1499392
println x

x = false
println x

x = "Hi"
println x
myList = [1776, -1, 33, 99, 0, 928734928763]
println myList.size()
scores = [ "Brett":100, "Pete":"Did not finish", "Andrew":86.87934 ]
println scores["Pete"]
println scores.Pete
emptyMap = [:]
emptyList = []
println emptyMap.size()
println emptyList.size()
amPM = Calendar.getInstance().get(Calendar.AM_PM)
if (amPM == Calendar.AM)
{
    println("Good morning")
} else {
    println("Good evening")
}