# Powerful Ultimate Binary Search Template 

Example - [875. Koko Eating Bananas](https://leetcode.com/problems/koko-eating-bananas/description/)

Here is the foundational code:

```java
public int minEatingSpeed(int[] piles, int H) {
    int l = 1, r = Arrays.stream(piles).max().getAsInt();
        while(l < r) {
            int m = l + (r - l)/2;
            int hSum = 0;
            for(int pile : piles)
                hSum += (int)Math.ceil((double)pile / m);
            if(hSum <= h)
                r = m;
            else
                l = m + 1;
        }
        return r;
}
```

---

## Intro

Binary Search is quite an easy concept to understand. Basically, it splits the search space into two halves and only keeps the half that probably has the search target, throwing away the other half. In this manner, we reduce the search space to half the size at every step, until we find the target. Binary Search helps us reduce search time from linear O(n) to logarithmic O(log n). But when it comes to implementation, it's rather difficult to write bug-free code in just a few minutes. Some of the most common problems include:

- When to exit the loop? Should we use `left < right` or `left <= right` as the while loop condition?
- How to initialize the boundary variables `left` and `right`?
- How to update the boundary? How to choose the appropriate combination from `left = mid`, `left = mid + 1`, and `right = mid`, `right = mid - 1`?

A rather common misunderstanding of binary search is that people often think this technique could only be used in simple scenarios like "Given a sorted array, find a specific value in it". As a matter of fact, it can be applied to much more complicated situations.

After a lot of practice, I've built a powerful binary search template and solved many Hard problems by just slightly twisting it. Most importantly, I want to share the logical thinking: how to apply this general template to all sorts of problems.

---

## Most Generalized Binary Search

Suppose we have a search space — it could be an array, a range, etc. Usually it's sorted in ascending order. For most tasks, we can transform the requirement into the following generalized form:

> **Minimize k, such that condition(k) is True**

The following code is the most generalized binary search template:

```java
public int binarySearch(int[] array) {
    int left = 0, right = array.length;
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (condition(mid)) right = mid;
        else left = mid + 1;
    }
    return left;
}

private boolean condition(int value) {
    // implement condition logic here
    return false;
}
```

What's really nice about this template is that for most binary search problems, we only need to modify three parts:

1. **Correctly initialize the boundary variables** `left` and `right`. Only one rule: set up the boundary to include all possible elements.
2. **Decide the return value.** Is it `return left` or `return left - 1`? Remember: after exiting the while loop, `left` is the minimal k satisfying the condition function.
3. **Design the condition function.** This is the most difficult and most beautiful part. It needs lots of practice.

---

## Basic Application

### [278. First Bad Version (Easy)](https://leetcode.com/problems/first-bad-version/description/)

You are a product manager currently leading a team to develop a new product. Since each version is developed based on the previous version, all the versions after a bad version are also bad. Suppose you have n versions `[1, 2, ..., n]` and you want to find out the first bad one. You are given an API `bool isBadVersion(version)` which returns whether a version is bad.

**Example:**

```
Given n = 5, and version = 4 is the first bad version.

call isBadVersion(3) -> false
call isBadVersion(5) -> true
call isBadVersion(4) -> true

Then 4 is the first bad version.
```

Finding the first bad version is equivalent to finding the minimal k satisfying `isBadVersion(k) is True`. Our template fits in very nicely:

```java
public int firstBadVersion(int n) {
    int left = 1, right = n;
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (isBadVersion(mid)) right = mid;
        else left = mid + 1;
    }
    return left;
}
```

---

### [69. Sqrt(x) (Easy)](https://leetcode.com/problems/sqrtx/)

Implement `int sqrt(int x)`. Compute and return the square root of x, where x is a non-negative integer. Since the return type is an integer, the decimal digits are truncated.

**Example:**

```
Input: 4  → Output: 2
Input: 8  → Output: 2
```

We search for the maximal k satisfying `k² ≤ x`:

```java
public int mySqrt(int x) {
    long left = 0, right = x;
    while (left < right) {
        long mid = left + (right - left) / 2;
        if (mid * mid <= x) left = mid + 1;
        else right = mid;
    }
    return (int)(left - 1);
}
```

> **Note:** The maximal k satisfying `condition(k) is False` equals the minimal k satisfying `condition(k) is True` minus one. This is why we sometimes return `left - 1` instead of `left`.

---

### [35. Search Insert Position (Easy)](https://leetcode.com/problems/search-insert-position/)

Given a sorted array and a target value, return the index if the target is found. If not, return the index where it would be if it were inserted in order. You may assume no duplicates in the array.

**Example:**

```
Input: [1,3,5,6], 5  → Output: 2
Input: [1,3,5,6], 2  → Output: 1
```

We look for the minimal k satisfying `nums[k] >= target`. Note that `right = len(nums)` (not `len - 1`) to handle the case where target is larger than all elements.

```java
public int searchInsert(int[] nums, int target) {
    int left = 0, right = nums.length;
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (nums[mid] >= target) right = mid;
        else left = mid + 1;
    }
    return left;
}
```

---

## Advanced Application

The above problems are easy to spot as binary search candidates. More often, the search space and target are not so readily available. Sometimes we won't even realize binary search applies — we might turn to dynamic programming or DFS and get stuck.

> **When can we use binary search?** If we can discover some kind of monotonicity — for example, if `condition(k) is True` then `condition(k + 1) is True` — then we can consider binary search.

---

### [1011. Capacity To Ship Packages Within D Days (Medium)](https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/)

A conveyor belt has packages that must all be shipped within D days. The i-th package weighs `weights[i]`. Return the least weight capacity of the ship that will result in all packages being shipped within D days.

**Example:**

```
Input: weights = [1,2,3,4,5,6,7,8,9,10], D = 5
Output: 15
```

We design a `feasible` function: given a capacity, determine whether it's possible to ship all packages within D days (greedy — fill current day, otherwise start next day).

```java
public int shipWithinDays(int[] weights, int D) {
    int left = 0, right = 0;
    for (int w : weights) {
        left = Math.max(left, w);
        right += w;
    }
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (feasible(weights, mid, D)) right = mid;
        else left = mid + 1;
    }
    return left;
}

private boolean feasible(int[] weights, int capacity, int D) {
    int days = 1, total = 0;
    for (int weight : weights) {
        total += weight;
        if (total > capacity) {
            total = weight;
            days++;
            if (days > D) return false;
        }
    }
    return true;
}
```

---

### [410. Split Array Largest Sum (Hard)](https://leetcode.com/problems/split-array-largest-sum/)

Given an array of non-negative integers and an integer m, split the array into m non-empty continuous subarrays to minimize the largest sum among these subarrays.

**Example:**

```
Input: nums = [7,2,5,10,8], m = 2
Output: 18
Explanation: Best split is [7,2,5] and [10,8], largest sum = 18.
```

Very similar to LC 1011 — the solution code is nearly identical:

```java
public int splitArray(int[] nums, int m) {
    int left = 0, right = 0;
    for (int n : nums) {
        left = Math.max(left, n);
        right += n;
    }
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (feasible(nums, mid, m)) right = mid;
        else left = mid + 1;
    }
    return left;
}

private boolean feasible(int[] nums, int threshold, int m) {
    int count = 1, total = 0;
    for (int num : nums) {
        total += num;
        if (total > threshold) {
            total = num;
            count++;
            if (count > m) return false;
        }
    }
    return true;
}
```

---

### [875. Koko Eating Bananas (Medium)](https://leetcode.com/problems/koko-eating-bananas/description/)

There are N piles of bananas. Koko can eat at speed K bananas per hour (one pile per hour). Return the minimum integer K such that she can eat all bananas within H hours.

**Example:**

```
Input: piles = [3,6,7,11], H = 8  → Output: 4
Input: piles = [30,11,23,4,20], H = 5  → Output: 30
```

```java
public int minEatingSpeed(int[] piles, int H) {
    int left = 1, right = 0;
    for (int pile : piles) right = Math.max(right, pile);

    while (left < right) {
        int mid = left + (right - left) / 2;
        if (feasible(piles, mid, H)) right = mid;
        else left = mid + 1;
    }
    return left;
}

private boolean feasible(int[] piles, int speed, int H) {
    int total = 0;
    for (int pile : piles) total += (pile - 1) / speed + 1;
    return total <= H;
}
```

---

### [1482. Minimum Number of Days to Make m Bouquets (Medium)](https://leetcode.com/problems/minimum-number-of-days-to-make-m-bouquets/)

Given `bloomDay[]`, integers m and k: make m bouquets, each requiring k adjacent bloomed flowers. Return the minimum number of days to wait, or -1 if impossible.

**Example:**

```
Input: bloomDay = [1,10,3,10,2], m = 3, k = 1  → Output: 3
Input: bloomDay = [1,10,3,10,2], m = 3, k = 2  → Output: -1
```

```java
public int minDays(int[] bloomDay, int m, int k) {
    if ((long) m * k > bloomDay.length) return -1;

    int left = 1, right = 0;
    for (int d : bloomDay) right = Math.max(right, d);

    while (left < right) {
        int mid = left + (right - left) / 2;
        if (feasible(bloomDay, mid, m, k)) right = mid;
        else left = mid + 1;
    }
    return left;
}

private boolean feasible(int[] bloomDay, int days, int m, int k) {
    int bouquets = 0, flowers = 0;
    for (int bloom : bloomDay) {
        if (bloom > days) {
            flowers = 0;
        } else {
            bouquets += (flowers + 1) / k;
            flowers = (flowers + 1) % k;
        }
    }
    return bouquets >= m;
}
```

---

### [668. Kth Smallest Number in Multiplication Table (Hard)](https://leetcode.com/problems/kth-smallest-number-in-multiplication-table/)

Given the height m and the length n of an m × n Multiplication Table and a positive integer k, return the k-th smallest number in the table.

**Example:**

```
Input: m = 3, n = 3, k = 5
Output: 3

Table:
1  2  3
2  4  6
3  6  9
The 5th smallest is 3 (1, 2, 2, 3, 3).
```

Instead of building the entire table (O(mn) space), we use binary search with an `enough` function that counts how many values are ≤ a given number by going row by row.

```java
public int findKthNumber(int m, int n, int k) {
    int left = 1, right = m * n;
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (enough(mid, m, n, k)) right = mid;
        else left = mid + 1;
    }
    return left;
}

private boolean enough(int num, int m, int n, int k) {
    int count = 0;
    for (int val = 1; val <= m; val++) {
        int add = Math.min(num / val, n);
        if (add == 0) break;
        count += add;
    }
    return count >= k;
}
```

---

### [719. Find K-th Smallest Pair Distance (Hard)](https://leetcode.com/problems/find-k-th-smallest-pair-distance/)

Given an integer array, return the k-th smallest distance among all pairs. The distance of a pair (A, B) is `|A - B|`.

**Example:**

```
Input: nums = [1,3,1], k = 1
Output: 0
```

Sort the array, then use binary search + two pointers for the `enough` function.

```java
public int smallestDistancePair(int[] nums, int k) {
    Arrays.sort(nums);
    int n = nums.length;
    int left = 0, right = nums[n - 1] - nums[0];
    while (left < right) {
        int mid = left + (right - left) / 2;
        if (enough(nums, mid, k)) right = mid;
        else left = mid + 1;
    }
    return left;
}

private boolean enough(int[] nums, int distance, int k) {
    int count = 0, i = 0, j = 0;
    int n = nums.length;
    while (i < n || j < n) {
        while (j < n && nums[j] - nums[i] <= distance) j++;
        count += j - i - 1;
        i++;
    }
    return count >= k;
}
```

---

### [1201. Ugly Number III (Medium)](https://leetcode.com/problems/ugly-number-iii/)

Write a program to find the n-th ugly number. Ugly numbers are positive integers divisible by a, b, or c.

**Example:**

```
Input: n = 3, a = 2, b = 3, c = 5  → Output: 4
Input: n = 4, a = 2, b = 3, c = 4  → Output: 6
```

Use inclusion-exclusion with GCD to avoid counting duplicates:

```java
public int nthUglyNumber(int n, int a, int b, int c) {
    long ab = lcm(a, b), ac = lcm(a, c), bc = lcm(b, c);
    long abc = lcm(a, bc);

    long left = 1, right = (long) 2e10;
    while (left < right) {
        long mid = left + (right - left) / 2;
        if (enough(mid, n, a, b, c, ab, ac, bc, abc)) right = mid;
        else left = mid + 1;
    }
    return (int) left;
}

private boolean enough(long num, int n, long a, long b, long c,
                        long ab, long ac, long bc, long abc) {
    long total = num/a + num/b + num/c - num/ab - num/ac - num/bc + num/abc;
    return total >= n;
}

private long lcm(long x, long y) {
    return x * y / gcd(x, y);
}

private long gcd(long x, long y) {
    return y == 0 ? x : gcd(y, x % y);
}
```

---

### [1283. Find the Smallest Divisor Given a Threshold (Medium)](https://leetcode.com/problems/find-the-smallest-divisor-given-a-threshold/)

Given an array of integers `nums` and an integer `threshold`, choose a positive integer divisor, divide all array elements by it (rounding up), and sum the results. Find the smallest divisor such that the sum is ≤ threshold.

**Example:**

```
Input: nums = [1,2,5,9], threshold = 6
Output: 5
```

```java
public int smallestDivisor(int[] nums, int threshold) {
    int left = 1, right = 0;
    for (int n : nums) right = Math.max(right, n);

    while (left < right) {
        int mid = left + (right - left) / 2;
        if (condition(nums, mid, threshold)) right = mid;
        else left = mid + 1;
    }
    return left;
}

private boolean condition(int[] nums, int divisor, int threshold) {
    int sum = 0;
    for (int num : nums) sum += (num - 1) / divisor + 1;
    return sum <= threshold;
}
```

---

## End

As you can see from the Java code above, all solutions look very similar to each other — because they all follow the same template. This is strong proof of the template's power. All we need is practice to:

1. Discover the **monotonicity** of the problem.
2. Design a beautiful **condition function**.

Hope this helps!
