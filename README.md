🔷 Optimal Polygon Triangulation

This project is a visual and algorithmic implementation of the Optimal Polygon Triangulation problem, a classic application of Dynamic Programming (DP) that shares its underlying mathematical structure with the Matrix Chain Multiplication (MCM) problem.

🌐 Live Demo

You can access the interactive web version of this project here:

Click Here to View Deployed Project (Note: Replace the URL above with your actual GitHub Pages, Vercel, or Netlify link)

📖 The Concept

The objective is to divide a convex polygon into non-overlapping triangles using diagonals such that the total "cost" of the triangulation is minimized.

In this implementation, the Cost (Weight) of a triangle $(v_i, v_j, v_k)$ is calculated as its perimeter:

$$Weight(i, j, k) = dist(v_i, v_j) + dist(v_j, v_k) + dist(v_i, v_k)$$

The DP Recurrence

We use a 2D table $t[n][n]$ to store minimum costs:

$$t[i, j] = \min_{i < k < j} \{ t[i, k] + t[k, j] + Weight(i, j, k) \}$$

Time Complexity: $O(n^3)$

Space Complexity: $O(n^2)$

🚀 Implementations

1. Web Version (HTML/CSS/JS)

An interactive browser-based application.

Features: Interactive canvas, real-time drawing, and automated vertex indexing.

Execution: Open index.html in any browser.

2. Java Version (Eclipse/Swing)

A desktop GUI application focused on manual input.

Features: Manual weight entry for exact algorithmic testing and emerald-green visual highlights.

Execution: Import the PolygonTriangulationApp.java into Eclipse and run as a Java Application.

3. C Version (Algorithm Core)

A console-based version focusing on the pure logic.

Features: Minimalist, fast, and terminal-driven.

Execution: ```bash
gcc optimal_triangulation.c -o triangulation
./triangulation




🛠️ How to Use

Define Vertices: Click on the canvas to place points in a circular order.

Assign Weights: Enter manual weights for sides or use the default geometric distances.

Solve: Trigger the calculation to compute the DP table.

Results: View the total minimum cost and the list of optimal "cuts" (diagonals).

👥 Project Information

Field: Design and Analysis of Algorithms (DAA)

Category: Computational Geometry & Resource Optimization

Environment: Eclipse IDE, Web Browser, GCC Compiler
