# 14 Patterns to Ace Any Coding Interview Question
## Table of Contents

1. [Sliding Window](#1-sliding-window)
2. [Two Pointers or Iterators](#2-two-pointers-or-iterators)
3. [Fast and Slow Pointers](#3-fast-and-slow-pointers)
4. [Merge Intervals](#4-merge-intervals)
5. [Cyclic Sort](#5-cyclic-sort)
6. [In-place Reversal of Linked List](#6-in-place-reversal-of-linked-list)
7. [Tree BFS](#7-tree-bfs)
8. [Tree DFS](#8-tree-dfs)
9. [Two Heaps](#9-two-heaps)
10. [Subsets](#10-subsets)
11. [Modified Binary Search](#11-modified-binary-search)
12. [Top K Elements](#12-top-k-elements)
13. [K-way Merge](#13-k-way-merge)
14. [Topological Sort](#14-topological-sort)

---

## 1. Sliding Window

The Sliding Window pattern is used to perform a required operation on a specific window size of a given array or linked list, such as finding the longest subarray containing all 1s. In some cases the window size remains constant; in others it grows or shrinks.

**How to identify:**
- Input is a linear data structure (linked list, array, or string)
- Asked to find the longest/shortest substring, subarray, or a desired value

**Example problems:** Maximum sum subarray of size K (easy) · Longest substring with K distinct characters (medium) · String anagrams (hard)

```java
// Maximum sum subarray of fixed size K
int maxSumSubarrayOfSizeK(int[] arr, int k) {
    int maxSum = 0, windowSum = 0;
    int windowStart = 0;

    for (int windowEnd = 0; windowEnd < arr.length; windowEnd++) {
        windowSum += arr[windowEnd]; // add the next element to the window

        if (windowEnd >= k - 1) { // slide the window once we hit size k
            maxSum = Math.max(maxSum, windowSum);
            windowSum -= arr[windowStart]; // remove the element going out
            windowStart++;
        }
    }
    return maxSum;
}
```

```java
// Longest substring with K distinct characters (variable window)
int longestSubstringKDistinct(String s, int k) {
    Map<Character, Integer> freq = new HashMap<>();
    int windowStart = 0, maxLen = 0;

    for (int windowEnd = 0; windowEnd < s.length(); windowEnd++) {
        char c = s.charAt(windowEnd);
        freq.merge(c, 1, Integer::sum);

        while (freq.size() > k) { // shrink window until we have at most k distinct chars
            char left = s.charAt(windowStart);
            freq.put(left, freq.get(left) - 1);
            if (freq.get(left) == 0) freq.remove(left);
            windowStart++;
        }
        maxLen = Math.max(maxLen, windowEnd - windowStart + 1);
    }
    return maxLen;
}
```

---

## 2. Two Pointers or Iterators

Two pointers iterate through the data structure in tandem until one or both hit a certain condition. Useful when searching pairs in a sorted array or linked list. Avoids the O(n²) brute-force back-and-forth with a single pointer.

**How to identify:**
- Sorted arrays (or linked lists) where you need to find elements fulfilling certain constraints
- The set of elements is a pair, a triplet, or a subarray

**Example problems:** Squaring a sorted array (easy) · Triplets that sum to zero (medium) · Comparing strings with backspaces (medium)

```java
// Pair with target sum in a sorted array
int[] pairWithTargetSum(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target)       return new int[]{left, right};
        else if (sum < target)   left++;
        else                     right--;
    }
    return new int[]{-1, -1};
}
```

```java
// Triplets that sum to zero
List<List<Integer>> searchTriplets(int[] arr) {
    Arrays.sort(arr);
    List<List<Integer>> triplets = new ArrayList<>();

    for (int i = 0; i < arr.length - 2; i++) {
        if (i > 0 && arr[i] == arr[i - 1]) continue; // skip duplicates
        int left = i + 1, right = arr.length - 1;
        while (left < right) {
            int sum = arr[i] + arr[left] + arr[right];
            if (sum == 0) {
                triplets.add(Arrays.asList(arr[i], arr[left], arr[right]));
                left++;  right--;
                while (left < right && arr[left] == arr[left - 1])  left++;
                while (left < right && arr[right] == arr[right + 1]) right--;
            } else if (sum < 0) left++;
            else                right--;
        }
    }
    return triplets;
}
```

---

## 3. Fast and Slow Pointers

Also known as the **Hare & Tortoise algorithm**. Two pointers move through the array or linked list at different speeds. Useful for cyclic linked lists or arrays — the fast pointer will eventually catch the slow pointer if a cycle exists.

**How to identify:**
- Problem deals with a loop in a linked list or array
- Need to know the position of a certain element or the overall length of the linked list
- Use over Two Pointers when you can't move backwards (e.g., singly linked list)

**Example problems:** Linked List Cycle (easy) · Palindrome Linked List (medium) · Cycle in a Circular Array (hard)

```java
// Detect cycle in a linked list
boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true; // cycle detected
    }
    return false;
}
```

```java
// Find middle of a linked list
ListNode findMiddle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow; // slow is now at the middle
}
```

```java
// Find start of cycle (Floyd's algorithm)
ListNode findCycleStart(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) {          // cycle found
            slow = head;             // reset one pointer to head
            while (slow != fast) {   // move both one step at a time
                slow = slow.next;
                fast = fast.next;
            }
            return slow;             // start of cycle
        }
    }
    return null;
}
```

---

## 4. Merge Intervals

An efficient technique to deal with overlapping intervals. Given two intervals `a` and `b`, there are six ways they can relate to each other. Understanding these cases lets you merge or find overlaps in O(n log n).

**How to identify:**
- Asked to produce a list with only mutually exclusive intervals
- Problem mentions "overlapping intervals"

**Example problems:** Intervals Intersection (medium) · Maximum CPU Load (hard)

```java
// Merge all overlapping intervals
int[][] merge(int[][] intervals) {
    if (intervals.length == 0) return intervals;

    Arrays.sort(intervals, (a, b) -> a[0] - b[0]); // sort by start time
    List<int[]> merged = new ArrayList<>();
    merged.add(intervals[0]);

    for (int i = 1; i < intervals.length; i++) {
        int[] last = merged.get(merged.size() - 1);
        int[] curr = intervals[i];

        if (curr[0] <= last[1]) {                       // overlapping
            last[1] = Math.max(last[1], curr[1]);       // merge
        } else {
            merged.add(curr);                           // non-overlapping
        }
    }
    return merged.toArray(new int[0][]);
}
```

```java
// Insert and merge a new interval into a sorted list
int[][] insertInterval(int[][] intervals, int[] newInterval) {
    List<int[]> result = new ArrayList<>();
    int i = 0, n = intervals.length;

    // add all intervals that come before the new interval
    while (i < n && intervals[i][1] < newInterval[0])
        result.add(intervals[i++]);

    // merge all overlapping intervals with the new one
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
        newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
        i++;
    }
    result.add(newInterval);

    // add remaining intervals
    while (i < n) result.add(intervals[i++]);
    return result.toArray(new int[0][]);
}
```

---

## 5. Cyclic Sort

Deals with problems involving arrays containing numbers in a given range `[1, n]`. Iterate over the array; if the current number is not at its correct index, swap it into place. Achieves O(n) time and O(1) space.

**How to identify:**
- Sorted array with numbers in a given range
- Problem asks for missing/duplicate/smallest number in a sorted/rotated array

**Example problems:** Find the Missing Number (easy) · Find the Smallest Missing Positive Number (medium)

```java
// Find the missing number in an array containing n-1 numbers from [0, n)
int findMissingNumber(int[] nums) {
    int i = 0;
    while (i < nums.length) {
        int j = nums[i]; // correct index for nums[i]
        if (nums[i] < nums.length && nums[i] != nums[j]) {
            int tmp = nums[i]; nums[i] = nums[j]; nums[j] = tmp; // swap
        } else {
            i++;
        }
    }
    // find the first index where nums[i] != i
    for (i = 0; i < nums.length; i++)
        if (nums[i] != i) return i;

    return nums.length; // all numbers present, missing is n
}
```

```java
// Find all duplicates in an array of range [1, n]
List<Integer> findAllDuplicates(int[] nums) {
    int i = 0;
    while (i < nums.length) {
        int j = nums[i] - 1; // correct index (1-indexed range)
        if (nums[i] != nums[j]) {
            int tmp = nums[i]; nums[i] = nums[j]; nums[j] = tmp;
        } else {
            i++;
        }
    }
    List<Integer> duplicates = new ArrayList<>();
    for (i = 0; i < nums.length; i++)
        if (nums[i] != i + 1) duplicates.add(nums[i]);

    return duplicates;
}
```

---

## 6. In-place Reversal of Linked List

Reverse a subset of nodes of a linked list in-place, without using extra memory. Uses a `current` pointer and a `previous` pointer that advance in lock-step while reversing links.

**How to identify:**
- Asked to reverse a linked list (or sub-list) without using extra memory

**Example problems:** Reverse a Sub-list (medium) · Reverse every K-element Sub-list (medium)

```java
// Reverse an entire linked list in-place
ListNode reverse(ListNode head) {
    ListNode prev = null, curr = head;
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
```

```java
// Reverse a sub-list from position p to q (1-indexed)
ListNode reverseSubList(ListNode head, int p, int q) {
    if (p == q) return head;

    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode prev = dummy;

    for (int i = 0; i < p - 1; i++) prev = prev.next; // move to node before p

    ListNode curr = prev.next;
    for (int i = 0; i < q - p; i++) {
        ListNode next = curr.next;
        curr.next = next.next;
        next.next = prev.next;
        prev.next = next;
    }
    return dummy.next;
}
```

---

## 7. Tree BFS

Based on Breadth First Search to traverse a tree level by level. Uses a queue to track all nodes of a level before jumping to the next.

**How to identify:**
- Asked to traverse a tree in level-by-level (level order) fashion

**Example problems:** Binary Tree Level Order Traversal (easy) · Zigzag Traversal (medium)

```java
// Level order traversal — returns list of levels
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>();

        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            currentLevel.add(node.val);
            if (node.left  != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(currentLevel);
    }
    return result;
}
```

```java
// Zigzag level order traversal
List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    boolean leftToRight = true;

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        LinkedList<Integer> currentLevel = new LinkedList<>();

        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            if (leftToRight) currentLevel.addLast(node.val);
            else             currentLevel.addFirst(node.val);

            if (node.left  != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(currentLevel);
        leftToRight = !leftToRight;
    }
    return result;
}
```

---

## 8. Tree DFS

Based on Depth First Search to traverse a tree. Uses recursion (or an explicit stack for iterative) to track parent nodes. Process nodes in pre-order, in-order, or post-order depending on the problem.

**How to identify:**
- Asked to traverse a tree with in-order, pre-order, or post-order DFS
- Problem requires searching for something where the node is closer to a leaf

**Example problems:** Sum of Path Numbers (medium) · All Paths for a Sum (medium)

```java
// Check if a root-to-leaf path with a given sum exists
boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;
    if (root.left == null && root.right == null) // leaf node
        return root.val == targetSum;

    return hasPathSum(root.left,  targetSum - root.val) ||
           hasPathSum(root.right, targetSum - root.val);
}
```

```java
// Find all root-to-leaf paths that sum to target
List<List<Integer>> pathSum(TreeNode root, int target) {
    List<List<Integer>> result = new ArrayList<>();
    dfs(root, target, new ArrayList<>(), result);
    return result;
}

void dfs(TreeNode node, int remaining, List<Integer> path, List<List<Integer>> result) {
    if (node == null) return;

    path.add(node.val);

    if (node.left == null && node.right == null && remaining == node.val) {
        result.add(new ArrayList<>(path)); // found a valid path
    } else {
        dfs(node.left,  remaining - node.val, path, result);
        dfs(node.right, remaining - node.val, path, result);
    }
    path.remove(path.size() - 1); // backtrack
}
```

---

## 9. Two Heaps

Divide a set of elements into two halves: a Max Heap for the lower half (to get the largest of the smaller half) and a Min Heap for the upper half (to get the smallest of the larger half). The median is always accessible from the tops.

**How to identify:**
- Need to find the smallest/largest/median elements of a set
- Useful in priority queues and scheduling problems

**Example problems:** Find the Median of a Number Stream (medium)

```java
class MedianFinder {
    PriorityQueue<Integer> maxHeap; // lower half (max-heap)
    PriorityQueue<Integer> minHeap; // upper half (min-heap)

    MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    void addNum(int num) {
        maxHeap.offer(num);                          // push to lower half
        minHeap.offer(maxHeap.poll());               // balance: push max of lower to upper
        if (minHeap.size() > maxHeap.size())         // keep lower half >= upper half in size
            maxHeap.offer(minHeap.poll());
    }

    double findMedian() {
        if (maxHeap.size() == minHeap.size())
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        return maxHeap.peek(); // lower half has one extra element
    }
}
```

---

## 10. Subsets

Use BFS to generate all subsets (power set), permutations, or combinations. Start with an empty set, then for each new number add it to all existing subsets to create new ones.

**How to identify:**
- Need to find combinations or permutations of a given set

**Example problems:** Subsets With Duplicates (easy) · String Permutations by changing case (medium)

```java
// Generate all subsets (power set)
List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> subsets = new ArrayList<>();
    subsets.add(new ArrayList<>()); // start with empty set

    for (int num : nums) {
        int n = subsets.size();
        for (int i = 0; i < n; i++) {
            List<Integer> newSubset = new ArrayList<>(subsets.get(i));
            newSubset.add(num);     // add current number to each existing subset
            subsets.add(newSubset);
        }
    }
    return subsets;
}
```

```java
// Generate all subsets with duplicates (sort first, skip duplicates)
List<List<Integer>> subsetsWithDuplicates(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> subsets = new ArrayList<>();
    subsets.add(new ArrayList<>());

    int start = 0, end = 0;
    for (int i = 0; i < nums.length; i++) {
        start = 0;
        if (i > 0 && nums[i] == nums[i - 1]) // duplicate: only extend previous iteration's subsets
            start = end + 1;
        end = subsets.size() - 1;
        for (int j = start; j <= end; j++) {
            List<Integer> newSubset = new ArrayList<>(subsets.get(j));
            newSubset.add(nums[i]);
            subsets.add(newSubset);
        }
    }
    return subsets;
}
```

---

## 11. Modified Binary Search

Whenever you are given a sorted array, linked list, or matrix and asked to find a certain element, use Binary Search. Calculate mid as `start + (end - start) / 2` to avoid integer overflow.

**How to identify:**
- Asked to find a certain element in a sorted array, linked list, or matrix

**Example problems:** Order-agnostic Binary Search (easy) · Search in a Sorted Infinite Array (medium)

```java
// Classic binary search (ascending order)
int binarySearch(int[] arr, int target) {
    int start = 0, end = arr.length - 1;
    while (start <= end) {
        int mid = start + (end - start) / 2; // avoids overflow
        if (arr[mid] == target)      return mid;
        else if (arr[mid] < target)  start = mid + 1;
        else                         end   = mid - 1;
    }
    return -1;
}
```

```java
// Order-agnostic binary search (works for ascending or descending)
int orderAgnosticBinarySearch(int[] arr, int target) {
    int start = 0, end = arr.length - 1;
    boolean isAscending = arr[start] < arr[end];

    while (start <= end) {
        int mid = start + (end - start) / 2;
        if (arr[mid] == target) return mid;

        if (isAscending) {
            if (target < arr[mid]) end   = mid - 1;
            else                   start = mid + 1;
        } else {
            if (target > arr[mid]) end   = mid - 1;
            else                   start = mid + 1;
        }
    }
    return -1;
}
```

```java
// Find first and last position of target in sorted array
int[] searchRange(int[] nums, int target) {
    return new int[]{ findFirst(nums, target), findLast(nums, target) };
}

int findFirst(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) { result = mid; hi = mid - 1; } // keep going left
        else if (nums[mid] < target) lo = mid + 1;
        else                         hi = mid - 1;
    }
    return result;
}

int findLast(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1, result = -1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) { result = mid; lo = mid + 1; } // keep going right
        else if (nums[mid] < target) lo = mid + 1;
        else                         hi = mid - 1;
    }
    return result;
}
```

---

## 12. Top K Elements

Find the top/smallest/most frequent K elements using a Heap. Insert elements into a min-heap (for top K largest) or max-heap (for top K smallest) of size K. If the heap exceeds size K, remove the root.

**How to identify:**
- Asked to find the top/smallest/frequent K elements of a given set
- Asked to sort an array to find an exact element

**Example problems:** Top K Numbers (easy) · Top K Frequent Numbers (medium)

```java
// Find K largest numbers using a min-heap of size K
int[] findKLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(); // min-heap

    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) minHeap.poll(); // remove smallest when heap exceeds k
    }

    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--)
        result[i] = minHeap.poll();
    return result;
}
```

```java
// Top K frequent elements
int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int n : nums) freq.merge(n, 1, Integer::sum);

    // min-heap ordered by frequency
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(Comparator.comparingInt(freq::get));

    for (int num : freq.keySet()) {
        minHeap.offer(num);
        if (minHeap.size() > k) minHeap.poll();
    }

    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--)
        result[i] = minHeap.poll();
    return result;
}
```

---

## 13. K-way Merge

Efficiently merge K sorted arrays/lists using a Min Heap. Push the first element of each list into the heap, then repeatedly pull the minimum and push the next element from that same list.

**How to identify:**
- Problem features sorted arrays, lists, or a matrix
- Asked to merge sorted lists or find the smallest element across sorted lists

**Example problems:** Merge K Sorted Lists (medium) · K Pairs with Largest Sums (hard)

```java
// Merge K sorted lists using a min-heap
ListNode mergeKLists(ListNode[] lists) {
    // min-heap ordered by node value
    PriorityQueue<ListNode> minHeap =
        new PriorityQueue<>(Comparator.comparingInt(a -> a.val));

    for (ListNode list : lists)
        if (list != null) minHeap.offer(list); // push head of each list

    ListNode dummy = new ListNode(0), curr = dummy;
    while (!minHeap.isEmpty()) {
        ListNode node = minHeap.poll();
        curr.next = node;
        curr = curr.next;
        if (node.next != null) minHeap.offer(node.next); // push next from same list
    }
    return dummy.next;
}
```

```java
// Kth smallest element in M sorted lists
int kthSmallest(int[][] lists, int k) {
    // [value, listIndex, elementIndex]
    PriorityQueue<int[]> minHeap = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

    for (int i = 0; i < lists.length; i++)
        if (lists[i].length > 0)
            minHeap.offer(new int[]{lists[i][0], i, 0});

    int count = 0, result = 0;
    while (!minHeap.isEmpty()) {
        int[] curr = minHeap.poll();
        result = curr[0];
        if (++count == k) break;

        int listIdx = curr[1], elemIdx = curr[2] + 1;
        if (elemIdx < lists[listIdx].length)
            minHeap.offer(new int[]{lists[listIdx][elemIdx], listIdx, elemIdx});
    }
    return result;
}
```

---

## 14. Topological Sort

Find a linear ordering of elements that have dependencies. Uses in-degree tracking (Kahn's algorithm): start from nodes with 0 in-degree, process them, reduce their neighbors' in-degrees, and enqueue any neighbor that reaches 0.

**How to identify:**
- Problem deals with graphs that have no directed cycles (DAGs)
- Asked to update all objects in a sorted order
- Objects follow a particular dependency order

**Example problems:** Task Scheduling (medium) · Minimum Height of a Tree (hard)

```java
// Topological sort — returns order or empty list if cycle detected
List<Integer> topologicalSort(int vertices, int[][] edges) {
    List<Integer> sortedOrder = new ArrayList<>();
    if (vertices <= 0) return sortedOrder;

    // 1. Initialize graph and in-degree count
    Map<Integer, List<Integer>> adj   = new HashMap<>();
    Map<Integer, Integer>       inDeg = new HashMap<>();
    for (int i = 0; i < vertices; i++) {
        adj.put(i, new ArrayList<>());
        inDeg.put(i, 0);
    }

    // 2. Build the graph
    for (int[] edge : edges) {
        int parent = edge[0], child = edge[1];
        adj.get(parent).add(child);
        inDeg.put(child, inDeg.get(child) + 1);
    }

    // 3. Collect all sources (in-degree == 0)
    Queue<Integer> sources = new LinkedList<>();
    for (Map.Entry<Integer, Integer> entry : inDeg.entrySet())
        if (entry.getValue() == 0) sources.offer(entry.getKey());

    // 4. BFS — process sources
    while (!sources.isEmpty()) {
        int vertex = sources.poll();
        sortedOrder.add(vertex);
        for (int child : adj.get(vertex)) {
            inDeg.put(child, inDeg.get(child) - 1);
            if (inDeg.get(child) == 0) sources.offer(child); // new source
        }
    }

    // if not all vertices are sorted, there is a cycle
    return sortedOrder.size() == vertices ? sortedOrder : new ArrayList<>();
}
```

```java
// Course Schedule — can you finish all courses given prerequisites?
boolean canFinish(int numCourses, int[][] prerequisites) {
    Map<Integer, List<Integer>> adj   = new HashMap<>();
    int[] inDeg = new int[numCourses];

    for (int i = 0; i < numCourses; i++) adj.put(i, new ArrayList<>());
    for (int[] pre : prerequisites) {
        adj.get(pre[1]).add(pre[0]);
        inDeg[pre[0]]++;
    }

    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < numCourses; i++)
        if (inDeg[i] == 0) queue.offer(i);

    int taken = 0;
    while (!queue.isEmpty()) {
        int course = queue.poll();
        taken++;
        for (int next : adj.get(course))
            if (--inDeg[next] == 0) queue.offer(next);
    }
    return taken == numCourses; // true = no cycle = can finish all
}
```

---

## Quick Pattern Cheat Sheet

| # | Pattern | Key Data Structure | Typical Complexity |
|---|---|---|---|
| 1 | Sliding Window | Array / String | O(N) |
| 2 | Two Pointers | Sorted Array / List | O(N) |
| 3 | Fast & Slow Pointers | Linked List | O(N) |
| 4 | Merge Intervals | Array + Sorting | O(N log N) |
| 5 | Cyclic Sort | Array (range [1,N]) | O(N) |
| 6 | In-place Linked List Reversal | Linked List | O(N) |
| 7 | Tree BFS | Queue | O(N) |
| 8 | Tree DFS | Recursion / Stack | O(N) |
| 9 | Two Heaps | PriorityQueue | O(N log N) |
| 10 | Subsets | List / BFS | O(N · 2ᴺ) |
| 11 | Modified Binary Search | Sorted Array | O(log N) |
| 12 | Top K Elements | PriorityQueue | O(N log K) |
| 13 | K-way Merge | PriorityQueue | O(N log K) |
| 14 | Topological Sort | Queue + HashMap | O(V + E) |
