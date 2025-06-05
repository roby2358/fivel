$.Index = function() {

  function addD(sides) {
    var r = Math.floor(Math.random() * sides) + 1;
    var i = Math.floor(Math.random() * 8999999999) + 10000000000
    var id = '#' + i
    $('div#rolls').append('<div id="' + i + '" class="roll left clicker">' + r + '</div>')
    $(id).click(() => $(id).remove())
  };

  function clearRolls() {
    $('div#rolls').empty();
  }

  function fetchOptions() {
    $.ajax({ url: '/tables', success: loadTables, dataType: 'json'});
    $.ajax({ url: '/words', success: loadWords, dataType: 'json'});
    $.ajax({ url: '/texts', success: loadTexts, dataType: 'json'});
  }

  function loadWords(data) {
    console.log('WORDS');
    console.log(data.words);
    $('select#words').empty();
    data.words.forEach(t =>
      $('select#words').append($('<option>', {
          value: t,
          text : t
      })));
  }

  function loadTables(data) {
    console.log('TABLES');
    console.log(data.tables);
    $('select#tables').empty();
    data.tables.forEach(t =>
      $('select#tables').append($('<option>', {
          value: t,
          text : t
      })));
  }

  function loadTexts(data) {
    console.log('TEXTS');
    console.log(data.texts);
    $('div#texts').empty();
    data.texts.forEach(t =>
      $('div#texts').append($('<p>' + t + '</p>')));
  }

  function rollTable() {
    var name = $('select#tables').val();
    console.log('roll ' + name);
    $.ajax({ url: '/tables/roll/' + name, success: loadRolled, dataType: 'json'});
  }

  function rollWord() {
    var name = $('select#words').val();
    console.log('roll ' + name);
    $.ajax({ url: '/words/roll/' + name, success: loadRolled, dataType: 'json'});
  }

  function randomId() {
    return Math.floor(Math.random() * 8999999999) + 10000000000;
  }

  function loadRolled(data) {
    var i = randomId();
    var id = '#' + i;
    var rolled = data.rolled.replace(/\n/g, '<br/>');
    var content = '<div id="' + i + '" class="clicker">' + rolled + '</div>';
    $('div#tableRolls').append(content);
    $(id).click(() => $(id).remove())
  }

  function clearTableRolls() {
    $('div#tableRolls').empty();
  }

  function copyTableRolls() {
    var html = '';
    var e = $('div#tableRolls').children().toArray().map(ee => ee.innerHTML).join("<br>");
    $(document).focus();
    navigator.clipboard.writeText(e.replace(/<br>/g, '\n'));
  }

  function show(tab) {
    ['dice', 'tables', 'words', 'texts'].forEach( t => {
      if (tab == t) {
        $('#' + t).addClass('sectionon').removeClass('sectionoff');
        $('#show' + t).addClass('tabon').removeClass('taboff');
      }
      else {
        $('#' + t).addClass('sectionoff').removeClass('sectionon');
        $('#show' + t).addClass('taboff').removeClass('tabon');
      }
    })
  }

  $('#showdice').click(() => show('dice'));
  $('#showtables').click(() => show('tables'));
  $('#showwords').click(() => show('words'));
  $('#showtext').click(() => show('text'));

  $('button#clear').click(clearRolls);
  $('select#tables').click(rollTable);
  $('select#words').click(rollWord);
  $('button#clearTableRolls').click(clearTableRolls);
  $('button#copyTableRolls').click(copyTableRolls);
  [100, 20, 12, 10, 8, 6, 4, 3].forEach(d =>
    $('button#d' + d).click(() => addD(d)) );

  fetchOptions();

  console.log('fivel ready!');
};