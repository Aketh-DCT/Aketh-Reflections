
var language = JSON.parse(AndroidImages.getLanguage())[0];
var wordU = JSON.parse(AndroidImages.getWord())[0];

var rows = 6;
var columns = 6;

function touchCallback(td, locations, array) {
  if (td.style.color === "rgb(153, 34, 153)") {
    return;
  }

  if (td.style.color !== "rgb(255, 255, 255)") {
    td.style.color = "#fff";
  } else {
    td.style.color = "#000";
  }

  console.log("gya");

  var count = 0;
  for (let x = 0; x < rows; x++) {
    for (let y = 0; y < columns; y++) {
      if (array[x][y].style.color === "rgb(255, 255, 255)") {
        count++;
      }
    }
  }

  console.log("Count:", count);

  for (let t of locations) {
    if (t[2] == count) {
      var countW = 0;
      for (let x = t[0]; x <= t[0] + t[2] - 1; x++) {
        console.log(array[t[1]][x], "Array", x);
        if (array[t[1]][x].style.color === "rgb(255, 255, 255)") {
          countW += 1;
        } else {
          console.log("lose");
        }
      }
    }
    if (countW == count) {
      console.log("Win");

      for (let x = t[0]; x <= t[0] + t[2] - 1; x++) {
        console.log(array[t[1]][x], "Array", x);
        if (array[t[1]][x].style.color === "rgb(255, 255, 255)") {
          array[t[1]][x].style.color = "#929";

          var tdToRemove = document.getElementById(array[t[1]][x].id);
          tdToRemove.removeEventListener("click", touchCallback);

          var points = 100; // Assume the points are calculated

          // Call the Android interface method
          AndroidGameInterface.onGameCompleted(points);
        } else {
        }
      }
    }
  }
}

function createClickListener(td, locations, array) {
  return function () {
    touchCallback(td, locations, array);
  };
}

document.addEventListener("DOMContentLoaded", function () {
  var lettersGreek = "αβγδεζηθικλμνξοπρστυφγψω"
  var lettersEnglish = "abcdefghijklmnopqrstuvwxyz"

  var letters = "";

  switch(language){
    case "el":
        letters = lettersGreek
        break;
    case "en":
        letters = lettersEnglish
        break;
    default:
        letters = lettersEnglish
  }



  var table = document.createElement("table");
  var tbody = document.createElement("tbody");

  table.appendChild(tbody);

  var arrayTable = [];
  var locations = [];

  //Add random elements in the javascript
  for (let index = 0; index < rows; index++) {
    var tr = document.createElement("tr");
    arrayTable[index] = [];
    for (let y = 0; y < columns; y++) {
      var td = document.createElement("td");

      td.textContent = letters
        .charAt(Math.floor(Math.random() * letters.length))
        .toUpperCase();
      td.style.background = "#ccc";
      arrayTable[index][y] = td;
      tr.appendChild(td);

      (function (td) {
        td.addEventListener("click", function () {
          touchCallback(td, locations, arrayTable);
        });

        var uniqueId = "td-" + Math.random().toString(36).substr(2, 9);
        td.setAttribute("id", uniqueId);
      })(td);
    }
    tbody.append(tr);
  }

  document.body.appendChild(table);

  putWordIn = function (word, array) {
    lengthW = word.length;
    locations = [];
    while (true) {
      rX = Math.floor(Math.random() * rows);
      rY = Math.floor(Math.random() * columns);

      if (rX + lengthW - 1 < rows) {
        currentX = rX;
        currentY = rY;
        for (let c of word) {
          //For now only horizontal
          array[currentY][currentX].textContent = c;
          currentX += 1;
        }
        console.log(
          "Current Location x",
          rX + 1,
          " -> ",
          rX + lengthW,
          " ,y: ",
          rY + 1
        );
        var orientationS = 1;
        locations.push([rX, rY, lengthW, orientationS, word]);

        console.log(lengthW);

        break;
      }
    }
    return locations;
  };

  locations = putWordIn(wordU.toUpperCase(), arrayTable);
  console.log(locations);
});

function adjustTableSize() {
  // Get viewport width and height
  var viewportWidth = window.innerWidth;
  var viewportHeight = window.innerHeight;

  // Calculate the smaller dimension
  var size = Math.min(viewportWidth, viewportHeight);

  // Get the table and table cells
  var table = document.querySelector('table');
  var cells = document.querySelectorAll('td');

  // Set the size of the table and cells
  table.style.width = size + 'px';
  table.style.height = size + 'px';
  cells.forEach(function(cell) {
    cell.style.width = (size / 6) + 'px';
    cell.style.height = (size / 6) + 'px';
    cell.style.fontSize = (size / 36) + 'px';
  });
}

// Adjust the table size when the page loads
adjustTableSize();

// Adjust the table size when the window is resized
window.addEventListener('resize', adjustTableSize);
